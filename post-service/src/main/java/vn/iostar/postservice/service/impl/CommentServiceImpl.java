package vn.iostar.postservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.postservice.constant.RoleName;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.GenericResponseAdmin;
import vn.iostar.postservice.dto.PaginationInfo;
import vn.iostar.postservice.dto.request.*;
import vn.iostar.postservice.dto.response.CommentPostResponse;
import vn.iostar.postservice.dto.response.CommentShareResponse;
import vn.iostar.postservice.dto.response.CommentsResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.entity.Share;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.CommentRepository;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.repository.ShareRepository;
import vn.iostar.postservice.service.CloudinaryService;
import vn.iostar.postservice.service.CommentService;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.ShareService;
import vn.iostar.postservice.service.client.UserClientService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

@Service
public class CommentServiceImpl extends RedisServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ShareRepository shareRepository;
    private final CloudinaryService cloudinaryService;
    private final UserClientService userClientService;
    private final JwtService jwtService;
    private final PostService postService;
    private final ShareService shareService;
    ObjectMapper objectMapper;

    public CommentServiceImpl(RedisTemplate<String, Object> redisTemplate, CommentRepository commentRepository, PostRepository postRepository, ShareRepository shareRepository, CloudinaryService cloudinaryService, UserClientService userClientService, JwtService jwtService, PostService postService, ShareService shareService) {
        super(redisTemplate);
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.shareRepository = shareRepository;
        this.cloudinaryService = cloudinaryService;
        this.userClientService = userClientService;
        this.jwtService = jwtService;
        this.postService = postService;
        this.shareService = shareService;
    }

    @Override
    public <S extends Comment> S save(S entity) {
        return commentRepository.save(entity);
    }

    @Override
    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    public ResponseEntity<GenericResponse> getCommentOfPost(String postId) throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        String indexStr = postId;
        if (this.hashExists("commentOfPost", indexStr)) {
            Object postsTimeline = this.hashGet("commentOfPost", indexStr);
            HashMap<String, Object> data = objectMapper.readValue((String) postsTimeline, HashMap.class);
            Object commentOfPost = data.get("commentOfPost");
            ArrayList<HashMap<String, Object>> pcommentOfPostList = (ArrayList<HashMap<String, Object>>) commentOfPost;
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving comment of post successfully")
                    .result(pcommentOfPostList).statusCode(HttpStatus.OK.value()).build());
        }
        Optional<Post> post = postService.findById(postId);
        if (post.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("Post not found").result(false)
                    .statusCode(HttpStatus.OK.value()).build());
        List<CommentPostResponse> comments = getCommentsOfPost(postId);
        if (comments.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("This post has no comment")
                    .result(false).statusCode(HttpStatus.OK.value()).build());
        HashMap<String, Object> response = new HashMap<>();
        response.put("commentOfPost", comments);
        String jsonData = objectMapper.writeValueAsString(response);
        this.hashSet("commentOfPost", indexStr, jsonData);
        return ResponseEntity
                .ok(GenericResponse.builder().success(true).message("Retrieving comment of post successfully")
                        .result(comments).statusCode(HttpStatus.OK.value()).build());
    }

    public List<CommentPostResponse> getCommentsOfPost(String postId) {
        List<Comment> commentPost = commentRepository
                .findByPostIdAndCommentReplyIsNullOrderByCreateTimeDesc(postId);

        List<CommentPostResponse> commentPostResponses = new ArrayList<>();
        for (Comment comment : commentPost) {
            UserProfileResponse userProfileResponse = userClientService.getUser(comment.getUserId());
            CommentPostResponse cPostResponse = new CommentPostResponse(comment, userProfileResponse);
            commentPostResponses.add(cPostResponse);
        }
        return commentPostResponses;
    }

    @Override
    public ResponseEntity<Object> createCommentPost(String token, CreateCommentPostRequestDTO requestDTO) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse userProfileResponse = userClientService.getUser(userId);
        if (userProfileResponse == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Post> post = postService.findById(requestDTO.getPostId());
        if (!post.isPresent()) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post.get());
        comment.setCreateTime(new Date());
        comment.setUpdatedAt(new Date());
        comment.setContent(requestDTO.getContent());
        try {
            if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
                comment.setPhotos("");
            } else {

                comment.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }

        comment.setUserId(userProfileResponse.getUserId());
        save(comment);

        // Thêm commentId mới vào danh sách comments của post
        List<String> postComments = post.get().getComments();
        if (postComments == null) {
            postComments = new ArrayList<>();
        }
        postComments.add(commentId);
        post.get().setComments(postComments);

        // Cập nhật lại post vào MongoDB
        postRepository.save(post.get());
        if (this.exists("commentOfPost")) this.delete("commentOfPost");
        if (this.exists("commentReplyOfCommentPost")) this.delete("commentReplyOfCommentPost");
        GenericResponse response = GenericResponse.builder().success(true).message("Comment Post Successfully")
                .result(new CommentPostResponse(comment.getId(), comment.getContent(), comment.getCreateTime(),
                        comment.getPhotos(), userProfileResponse.getUserName(),comment.getPost().getId(), userProfileResponse.getAvatar(),  userProfileResponse.getUserId()))
                .statusCode(200).build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> replyCommentPost(String token, ReplyCommentPostRequestDTO requestDTO) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Post> post = postService.findById(requestDTO.getPostId());
        if (!post.isPresent()) {
            return ResponseEntity.badRequest().body("Post not found");
        }
        Optional<Comment> commentReply = findById(String.valueOf(requestDTO.getCommentId()));
        if (!commentReply.isPresent()) {
            return ResponseEntity.badRequest().body("Comment not found");
        }

        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post.get());
        comment.setCreateTime(new Date());
        comment.setUpdatedAt(new Date());
        comment.setContent(requestDTO.getContent());
        try {
            if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
                comment.setPhotos("");
            } else {

                comment.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }
        comment.setUserId(user.getUserId());
        comment.setCommentReply(commentReply.get());
        save(comment);

        // Thêm commentId mới vào danh sách comments của post
        List<String> postComments = post.get().getComments();
        if (postComments == null) {
            postComments = new ArrayList<>();
        }
        postComments.add(commentId);
        post.get().setComments(postComments);

        // Cập nhật lại post vào MongoDB
        postRepository.save(post.get());
        if (this.exists("commentOfPost")) this.delete("commentOfPost");
        if (this.exists("commentReplyOfCommentPost")) this.delete("commentReplyOfCommentPost");
        GenericResponse response = GenericResponse.builder().success(true).message("Comment Post Successfully")
                .result(new CommentPostResponse(comment.getId(), comment.getContent(), comment.getCreateTime(),
                        comment.getPhotos(), user.getUserName(), comment.getPost().getId(),
                        user.getAvatar(), user.getUserId()))
                .statusCode(200).build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> updateComment(String commentId, CommentUpdateRequest request, String currentUserId)
            throws Exception {

        Optional<Comment> commentOp = findById(commentId);
        if (commentOp.isEmpty())
            throw new Exception("Comment doesn't exist");
        Comment comment = commentOp.get();
        if (!currentUserId.equals(commentOp.get().getUserId()))
            throw new Exception("Update denied");
        comment.setContent(request.getContent());
        try {
            if (request.getPhotos() == null || request.getPhotos().getContentType() == null) {
                comment.setPhotos("");
            } else if (request.getPhotos().equals(commentOp.get().getPhotos())) {
                comment.setPhotos(commentOp.get().getPhotos());
            } else {
                comment.setPhotos(cloudinaryService.uploadImage(request.getPhotos()));
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }
        comment.setUpdatedAt(new Date());
        save(comment);
        if (this.exists("commentOfPost")) this.delete("commentOfPost");
        if (this.exists("commentReplyOfCommentPost")) this.delete("commentReplyOfCommentPost");
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful").result(null)
                .statusCode(200).build());
    }

    @Override
    @Transactional
    public ResponseEntity<GenericResponse> deleteCommentOfPost(String commentId) {
        Optional<Comment> optionalComment = findById(commentId);

        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();

            // Xóa các comments có commentReply là id của comment vừa xóa
            List<Comment> commentsWithReply = commentRepository.findByCommentReplyIdOrderByCreateTimeDesc(commentId);
            //commentRepository.deleteAll(commentsWithReply);

            List<CommentPostResponse> comments = getCommentsOfComment(commentId);
            for (CommentPostResponse c : comments) {
                Optional<Comment> cmt = commentRepository.findById(c.getCommentId());
                if (cmt.isPresent()) {
                    commentRepository.delete(cmt.get());
                }
            }

            // Xóa comment
            commentRepository.delete(comment);

            // Lấy post của comment
            Post post = comment.getPost();

            if (post != null) {
                // Lấy danh sách comments của post
                List<String> postComments = post.getComments();

                if (postComments != null) {
                    // Kiểm tra xem danh sách comments của post có null hay không
                    // Nếu là null, khởi tạo một danh sách mới
                    if (postComments == null) {
                        postComments = new ArrayList<>();
                    }

                    // Xóa commentId khỏi danh sách comments của post
                    postComments.remove(commentId);

                    // Cập nhật lại danh sách comments của post
                    post.setComments(postComments);

                    for (Comment c : commentsWithReply) {
                        postComments.remove(c.getId());

                        for (Comment directReply : commentsWithReply) {
                            postComments.remove(directReply.getId());
                        }
                    }

                    for (CommentPostResponse c : comments) {
                        postComments.remove(c.getCommentId());
                    }

                    // Cập nhật lại post vào MongoDB
                    postRepository.save(post);
                }
            }

            Share share = comment.getShare();
            if (share != null) {
                // Lấy danh sách comments của share
                List<String> shareComments = share.getComments();

                if (shareComments != null) {
                    // Kiểm tra xem danh sách comments của share có null hay không
                    // Nếu là null, khởi tạo một danh sách mới
                    if (shareComments == null) {
                        shareComments = new ArrayList<>();
                    }

                    // Xóa commentId khỏi danh sách comments của share
                    shareComments.remove(commentId);

                    // Cập nhật lại danh sách comments của share
                    share.setComments(shareComments);

                    for (Comment c : commentsWithReply) {
                        shareComments.remove(c.getId());
                        for (Comment directReply : commentsWithReply) {
                            shareComments.remove(directReply.getId());
                        }
                    }

                    for (CommentPostResponse c : comments) {
                        shareComments.remove(c.getCommentId());
                    }

                    // Cập nhật lại share vào MongoDB
                    shareRepository.save(share);
                }
            }
            if (this.exists("commentOfPost")) this.delete("commentOfPost");
            if (this.exists("commentReplyOfCommentPost")) this.delete("commentReplyOfCommentPost");
            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found comment!", null, HttpStatus.NOT_FOUND.value()));
        }
    }


    @Override
    public ResponseEntity<GenericResponse> getCommentReplyOfComment(String commentId) throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        String indexStr = commentId;
        if (this.hashExists("commentReplyOfCommentPost", indexStr)) {
            Object postsTimeline = this.hashGet("commentReplyOfCommentPost", indexStr);
            HashMap<String, Object> data = objectMapper.readValue((String) postsTimeline, HashMap.class);
            Object commentReplyOfCommentPostObj = data.get("commentReplyOfCommentPost");
            ArrayList<HashMap<String, Object>> commentReplyOfCommentPostList = (ArrayList<HashMap<String, Object>>) commentReplyOfCommentPostObj;
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving comment of post successfully from redis")
                    .result(commentReplyOfCommentPostList).statusCode(HttpStatus.OK.value()).build());
        }
        Optional<Comment> comment = findById(commentId);
        if (comment.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("Comment not found").result(false)
                    .statusCode(HttpStatus.OK.value()).build());
        List<CommentPostResponse> comments = getCommentsOfComment(commentId);
        if (comments.isEmpty())
            return ResponseEntity
                    .ok(GenericResponse.builder().success(false).message("This comment has no comment reply")
                            .result(false).statusCode(HttpStatus.OK.value()).build());
        HashMap<String, Object> response = new HashMap<>();
        response.put("commentReplyOfCommentPost", comments);
        String jsonData = objectMapper.writeValueAsString(response);
        this.hashSet("commentReplyOfCommentPost", indexStr, jsonData);
        return ResponseEntity
                .ok(GenericResponse.builder().success(true).message("Retrieving comment of post successfully")
                        .result(comments).statusCode(HttpStatus.OK.value()).build());
    }

    public List<CommentPostResponse> getCommentsOfComment(String commentId) {
        List<CommentPostResponse> commentPostResponses = new ArrayList<>();

        // Tìm các comment reply trực tiếp cho commentId
        List<Comment> directReplies = commentRepository.findByCommentReplyIdOrderByCreateTimeDesc(commentId);

        // Lấy comment reply của commentId
        for (Comment directReply : directReplies) {
            UserProfileResponse userProfileResponse = userClientService.getUser(directReply.getUserId());
            CommentPostResponse directReplyResponse = new CommentPostResponse(directReply,userProfileResponse);
            directReplyResponse.setUserOwner(userProfileResponse.getUserName());
            commentPostResponses.add(directReplyResponse);

            // Tìm các comment reply cho directReply
            List<CommentPostResponse> subReplies = getCommentsOfComment(directReply.getId());

            // Thêm tất cả các comment reply của directReply
            commentPostResponses.addAll(subReplies);
        }

        return commentPostResponses;
    }

    @Override
    public ResponseEntity<GenericResponse> getCountCommentOfPost(String postId) {
        Optional<Post> post = postService.findById(postId);
        Optional<Share> share = shareService.findById(postId);
        List<Comment> comments = new ArrayList<>();
        if (!post.isEmpty())  {
            comments = commentRepository.findByPostIdOrderByCreateTimeDesc(postId);
        } else if (!share.isEmpty()) {
            comments = commentRepository.findByShareIdOrderByCreateTimeDesc(postId);
        }
        if (!comments.isEmpty()) {
            List<CommentPostResponse> commentPostResponses = new ArrayList<>();
            for (Comment comment : comments) {
                UserProfileResponse userProfileResponse = userClientService.getUser(comment.getUserId());
                commentPostResponses.add(new CommentPostResponse(comment, userProfileResponse));
            }
            return ResponseEntity.ok(
                    GenericResponse.builder().success(true).message("Retrieving number of comments of Post successfully")
                            .result(commentPostResponses.size()).statusCode(HttpStatus.OK.value()).build());
        } else {
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("This post has no comment")
                    .result(0).statusCode(HttpStatus.OK.value()).build());
        }
    }

    @Override
    public ResponseEntity<GenericResponse> getCommentOfShare(String shareId) {
        Optional<Share> share = shareService.findById(shareId);
        if (share.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("Share not found").result(false)
                    .statusCode(HttpStatus.OK.value()).build());
        List<CommentShareResponse> comments = getCommentsOfShare(shareId);
        if (comments.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("This share has no comment")
                    .result(false).statusCode(HttpStatus.OK.value()).build());
        return ResponseEntity
                .ok(GenericResponse.builder().success(true).message("Retrieving comment of share post successfully")
                        .result(comments).statusCode(HttpStatus.OK.value()).build());
    }

    public List<CommentShareResponse> getCommentsOfShare(String shareId) {
        List<Comment> commentPost = commentRepository
                .findByShareIdAndCommentReplyIsNullOrderByCreateTimeDesc(shareId);

        List<CommentShareResponse> commentPostResponses = new ArrayList<>();
        for (Comment comment : commentPost) {
            UserProfileResponse userProfileResponse = userClientService.getUser(comment.getUserId());
            CommentShareResponse cPostResponse = new CommentShareResponse(comment, userProfileResponse);
            commentPostResponses.add(cPostResponse);
        }
        return commentPostResponses;
    }

    @Override
    public ResponseEntity<GenericResponse> getCommentReplyOfCommentShare(String commentId) {
        Optional<Comment> comment = findById(commentId);
        if (comment.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("Comment not found").result(false)
                    .statusCode(HttpStatus.OK.value()).build());
        List<CommentShareResponse> comments = getCommentsOfCommentShare(commentId);
        if (comments.isEmpty())
            return ResponseEntity
                    .ok(GenericResponse.builder().success(false).message("This comment has no comment reply")
                            .result(false).statusCode(HttpStatus.OK.value()).build());
        return ResponseEntity
                .ok(GenericResponse.builder().success(true).message("Retrieving comment of share post successfully")
                        .result(comments).statusCode(HttpStatus.OK.value()).build());
    }

    public List<CommentShareResponse> getCommentsOfCommentShare(String commentId) {

        List<CommentShareResponse> commentPostResponses = new ArrayList<>();

        // Tìm các comment reply trực tiếp cho commentId
        List<Comment> directReplies = commentRepository.findByCommentReplyIdOrderByCreateTimeDesc(commentId);

        // Lấy comment reply của commentId
        for (Comment directReply : directReplies) {
            UserProfileResponse userProfileResponse = userClientService.getUser(directReply.getUserId());
            CommentShareResponse directReplyResponse = new CommentShareResponse(directReply,userProfileResponse);
            directReplyResponse.setUserOwner(userProfileResponse.getUserName());
            commentPostResponses.add(directReplyResponse);
        }

        return commentPostResponses;
    }

    @Override
    public ResponseEntity<Object> createCommentShare(String token, CreateCommentShareRequestDTO requestDTO) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Share> share = shareService.findById(requestDTO.getShareId());
        if (!share.isPresent()) {
            return ResponseEntity.badRequest().body("Share not found");
        }

        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setShare(share.get());
        comment.setCreateTime(new Date());
        comment.setUpdatedAt(new Date());
        comment.setContent(requestDTO.getContent());
        try {
            if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
                comment.setPhotos("");
            } else {

                comment.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }
        comment.setUserId(user.getUserId());
        save(comment);

        // Thêm commentId mới vào danh sách comments của post
        List<String> shareComments = share.get().getComments();
        if (shareComments == null) {
            shareComments = new ArrayList<>();
        }
        shareComments.add(commentId);
        share.get().setComments(shareComments);

        // Cập nhật lại share vào MongoDB
        shareRepository.save(share.get());

        GenericResponse response = GenericResponse.builder().success(true).message("Comment Share Successfully")
                .result(new CommentShareResponse(comment.getId(), comment.getContent(), comment.getCreateTime(),
                        comment.getPhotos(), user.getUserName(), comment.getShare().getId(),
                        user.getAvatar(), user.getUserId()))
                .statusCode(200).build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> replyCommentShare(String token, ReplyCommentShareRequestDTO requestDTO) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Share> share = shareService.findById(requestDTO.getShareId());
        if (!share.isPresent()) {
            return ResponseEntity.badRequest().body("Share post not found");
        }
        Optional<Comment> commentReply = findById(requestDTO.getCommentId());
        if (!commentReply.isPresent()) {
            return ResponseEntity.badRequest().body("Comment not found");
        }

        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setShare(share.get());
        comment.setCreateTime(new Date());
        comment.setUpdatedAt(new Date());
        comment.setContent(requestDTO.getContent());
        try {
            if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
                comment.setPhotos("");
            } else {

                comment.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }
        comment.setUserId(user.getUserId());
        comment.setCommentReply(commentReply.get());
        save(comment);

        // Thêm commentId mới vào danh sách comments của share
        List<String> shareComments = share.get().getComments();
        if (shareComments == null) {
            shareComments = new ArrayList<>();
        }
        shareComments.add(commentId);
        share.get().setComments(shareComments);

        // Cập nhật lại share vào MongoDB
        shareRepository.save(share.get());

        GenericResponse response = GenericResponse.builder().success(true).message("Reply Comment Share Post Successfully")
                .result(new CommentShareResponse(comment.getId(), comment.getContent(), comment.getCreateTime(),
                        comment.getPhotos(), user.getUserName(), comment.getShare().getId(),
                        user.getAvatar(), user.getUserId()))
                .statusCode(200).build();

        return ResponseEntity.ok(response);
    }

    @Override
    public Streamable<Object> findAllComments(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<Comment> commentsPage = commentRepository.findAllByOrderByCreateTimeDesc(pageable);

        return commentsPage.map(comment -> {
            UserProfileResponse userProfileResponse = userClientService.getUser(comment.getUserId());
            if (comment.getPost() != null && comment.getPost().getId() != null) {
                return new CommentPostResponse(comment, userProfileResponse);
            } else {
                return new CommentShareResponse(comment, userProfileResponse);
            }
        });
    }

    @Override
    public ResponseEntity<GenericResponseAdmin> getAllComments(String authorizationHeader, int page, int itemsPerPage) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        UserProfileResponse user = userClientService.getUser(currentUserId);
        RoleName roleName = user.getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }

        Streamable<Object> commentsPage = findAllComments(page, itemsPerPage);
        long totalComments = commentRepository.count();

        PaginationInfo pagination = new PaginationInfo();
        pagination.setPage(page);
        pagination.setItemsPerPage(itemsPerPage);
        pagination.setCount(totalComments);
        pagination.setPages((int) Math.ceil((double) totalComments / itemsPerPage));

        if (commentsPage.isEmpty()) {
            return ResponseEntity
                    .ok(GenericResponseAdmin.builder().success(true).message("No Comments Found")
                            .result(commentsPage).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
        } else {
            return ResponseEntity
                    .ok(GenericResponseAdmin.builder().success(true).message("Retrieved List Comments Successfully")
                            .result(commentsPage).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<GenericResponse> deleteCommentByAdmin(String commentId, String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        UserProfileResponse user = userClientService.getUser(currentUserId);
        RoleName roleName = user.getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
        Optional<Comment> optionalComment = findById(commentId);
        Streamable<Object> commentsPage = findAllComments(1, 10);
        // Tìm thấy bài comment với commentId
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            // Xóa các comments có commentReply là id của comment vừa xóa
            List<Comment> commentsWithReply = commentRepository.findByCommentReplyIdOrderByCreateTimeDesc(commentId);
            commentRepository.deleteAll(commentsWithReply);

            Post post = comment.getPost();
            if (post != null) {
                // Lấy danh sách comments của post
                List<String> postComments = post.getComments();

                if (postComments != null) {
                    // Kiểm tra xem danh sách comments của post có null hay không
                    // Nếu là null, khởi tạo một danh sách mới
                    if (postComments == null) {
                        postComments = new ArrayList<>();
                    }

                    // Xóa commentId khỏi danh sách comments của post
                    postComments.remove(commentId);

                    // Cập nhật lại danh sách comments của post
                    post.setComments(postComments);

                    for (Comment c : commentsWithReply) {
                        postComments.remove(c.getId());
                    }

                    // Cập nhật lại post vào MongoDB
                    postRepository.save(post);
                }
            }
            Share share = comment.getShare();
            if (share != null) {
                // Lấy danh sách comments của share
                List<String> shareComments = share.getComments();

                if (shareComments != null) {
                    // Kiểm tra xem danh sách comments của share có null hay không
                    // Nếu là null, khởi tạo một danh sách mới
                    if (shareComments == null) {
                        shareComments = new ArrayList<>();
                    }

                    // Xóa commentId khỏi danh sách comments của share
                    shareComments.remove(commentId);

                    // Cập nhật lại danh sách comments của share
                    share.setComments(shareComments);

                    for (Comment c : commentsWithReply) {
                        shareComments.remove(c.getId());
                    }

                    // Cập nhật lại share vào MongoDB
                    shareRepository.save(share);
                }
            }
            commentRepository.delete(comment);
            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", commentsPage, HttpStatus.OK.value()));
        }
        // Khi không tìm thấy comment với id
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found comment!", null, HttpStatus.NOT_FOUND.value()));
        }
    }

    // Đếm số lượng comment từng tháng trong năm
    @Override
    public Map<String, Long> countCommentsByMonthInYear() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        // Tạo một danh sách các tháng
        List<Month> months = Arrays.asList(Month.values());
        Map<String, Long> commentCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

        for (Month month : months) {
            LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

            Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

            long commentCount = commentRepository.countByCreateTimeBetween(startDateAsDate, endDateAsDate);
            commentCountsByMonth.put(month.toString(), commentCount);
        }

        return commentCountsByMonth;
    }

    // Đếm số lượng comment trong 1 năm
    @Override
    public long countCommentsInOneYearFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusYears(1);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return commentRepository.countByCreateTimeBetween(startDateAsDate, endDateAsDate);
    }

    // Chuyển sang giờ bắt đầu của 1 ngày là 00:00:00
    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // Chuyển sang giờ kết thức của 1 ngày là 23:59:59
    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private Date getNDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    // Chuyển từ kiểu Comment sang CommentsResponse
    private List<CommentsResponse> mapToCommentsResponseList(List<Comment> comments) {
        List<CommentsResponse> responses = new ArrayList<>();
        for (Comment comment : comments) {
            UserProfileResponse userProfileResponse = userClientService.getUser(comment.getUserId());
            CommentsResponse postsResponse = new CommentsResponse(comment, userProfileResponse);
            responses.add(postsResponse);
        }
        return responses;
    }

    @Override
    public List<CommentsResponse> getCommentsToday() {
        Date startDate = getStartOfDay(new Date());
        Date endDate = getEndOfDay(new Date());
        List<Comment> comments = commentRepository.findByCreateTimeBetween(startDate, endDate);
        return mapToCommentsResponseList(comments);
    }

    @Override
    public List<CommentsResponse> getCommentsIn7Days() {
        Date startDate = getStartOfDay(getNDaysAgo(6));
        Date endDate = getEndOfDay(new Date());
        List<Comment> comments = commentRepository.findByCreateTimeBetween(startDate, endDate);
        return mapToCommentsResponseList(comments);
    }

    @Override
    public List<CommentsResponse> getCommentsIn1Month() {
        Date startDate = getStartOfDay(getNDaysAgo(30));
        Date endDate = getEndOfDay(new Date());
        List<Comment> comments = commentRepository.findByCreateTimeBetween(startDate, endDate);
        return mapToCommentsResponseList(comments);
    }

    @Override
    public Streamable<Object> findAllCommentsByUserId(int page, int itemsPerPage, String userId) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<Comment> commentsPage = commentRepository.findAllByUserIdOrderByCreateTimeDesc(userId, pageable);

        Streamable<Object> commentResponsesPage = commentsPage.map(comment -> {
            if (comment.getPost() != null && comment.getPost().getId() != null) {
                UserProfileResponse userProfileResponse = userClientService.getUser(comment.getUserId());
                CommentPostResponse cPostResponse = new CommentPostResponse(comment, userProfileResponse);
                return cPostResponse;
            } else if (comment.getShare() != null && comment.getShare().getId() != null) {
                UserProfileResponse userProfileResponse = userClientService.getUser(comment.getUserId());
                CommentShareResponse cShareResponse = new CommentShareResponse(comment, userProfileResponse);
                return cShareResponse;
            }
            return null;
        }); // Lọc bất kỳ giá trị null nào nếu có
        return commentResponsesPage;
    }


    // Đếm số lượng comment của 1 user từng tháng trong năm
    @Override
    public Map<String, Long> countCommentsByUserMonthInYear(String userId) {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        UserProfileResponse user = userClientService.getUser(userId);

        // Tạo một danh sách các tháng
        List<Month> months = Arrays.asList(Month.values());
        Map<String, Long> commentCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

        for (Month month : months) {
            LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

            Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

            long commentCount = commentRepository.countByUserIdAndCreateTimeBetween(user.getUserId(),startDateAsDate, endDateAsDate);
            commentCountsByMonth.put(month.toString(), commentCount);
        }

        return commentCountsByMonth;
    }

}
