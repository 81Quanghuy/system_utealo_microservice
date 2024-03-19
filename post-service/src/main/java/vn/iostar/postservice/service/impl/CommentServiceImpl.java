package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CommentUpdateRequest;
import vn.iostar.postservice.dto.request.CreateCommentPostRequestDTO;
import vn.iostar.postservice.dto.request.ReplyCommentPostRequestDTO;
import vn.iostar.postservice.dto.response.CommentPostResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.CommentRepository;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.service.CloudinaryService;
import vn.iostar.postservice.service.CommentService;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.client.UserClientService;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CloudinaryService cloudinaryService;
    private final UserClientService userClientService;
    private final JwtService jwtService;
    private final PostService postService;

    @Override
    public <S extends Comment> S save(S entity) {
        return commentRepository.save(entity);
    }

    @Override
    public Optional<Comment> findById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    public ResponseEntity<GenericResponse> getCommentOfPost(String postId) {
        Optional<Post> post = postService.findById(postId);
        if (post.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("Post not found").result(false)
                    .statusCode(HttpStatus.OK.value()).build());
        List<CommentPostResponse> comments = getCommentsOfPost(postId);
        if (comments.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("This post has no comment")
                    .result(false).statusCode(HttpStatus.OK.value()).build());
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
            List<Comment> commentsWithReply = commentRepository.findByCommentReplyId(commentId);
            commentRepository.deleteAll(commentsWithReply);

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
                    }

                    // Cập nhật lại post vào MongoDB
                    postRepository.save(post);
                }
            }

            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found comment!", null, HttpStatus.NOT_FOUND.value()));
        }
    }


    @Override
    public ResponseEntity<GenericResponse> getCommentReplyOfComment(String commentId) {
        Optional<Comment> comment = findById(commentId);
        if (comment.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(false).message("Comment not found").result(false)
                    .statusCode(HttpStatus.OK.value()).build());
        List<CommentPostResponse> comments = getCommentsOfComment(commentId);
        if (comments.isEmpty())
            return ResponseEntity
                    .ok(GenericResponse.builder().success(false).message("This comment has no comment reply")
                            .result(false).statusCode(HttpStatus.OK.value()).build());
        return ResponseEntity
                .ok(GenericResponse.builder().success(true).message("Retrieving comment of post successfully")
                        .result(comments).statusCode(HttpStatus.OK.value()).build());
    }

    public List<CommentPostResponse> getCommentsOfComment(String commentId) {
        List<CommentPostResponse> commentPostResponses = new ArrayList<>();

        // Tìm các comment reply trực tiếp cho commentId
        List<Comment> directReplies = commentRepository.findByCommentReplyId(commentId);

        // Lấy comment reply của commentId
        for (Comment directReply : directReplies) {
            UserProfileResponse userProfileResponse = userClientService.getUser(directReply.getUserId());
            CommentPostResponse directReplyResponse = new CommentPostResponse(directReply,userProfileResponse);
            directReplyResponse.setUserOwner(userProfileResponse.getUserName());
            commentPostResponses.add(directReplyResponse);

        }

        return commentPostResponses;
    }

    @Override
    public ResponseEntity<GenericResponse> getCountCommentOfPost(String postId) {
        Optional<Post> post = postService.findById(postId);
        if (post.isEmpty())
            throw new RuntimeException("Post not found");
        List<Comment> comments = commentRepository.findByPostIdAndCommentReplyIsNullOrderByCreateTimeDesc(postId);
        if (comments.isEmpty())
            throw new RuntimeException("This post has no comment");
        List<CommentPostResponse> commentPostResponses = new ArrayList<>();
        for (Comment comment : comments) {
            UserProfileResponse userProfileResponse = userClientService.getUser(comment.getUserId());
            commentPostResponses.add(new CommentPostResponse(comment,userProfileResponse));
        }
        return ResponseEntity.ok(
                GenericResponse.builder().success(true).message("Retrieving number of comments of Post successfully")
                        .result(commentPostResponses.size()).statusCode(HttpStatus.OK.value()).build());
    }
}
