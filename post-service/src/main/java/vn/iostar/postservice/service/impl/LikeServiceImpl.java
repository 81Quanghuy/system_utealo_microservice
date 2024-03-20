package vn.iostar.postservice.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.dto.LikePostResponse;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.response.ListUserLikePost;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.entity.Like;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.entity.Share;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.LikeRepository;
import vn.iostar.postservice.repository.LikeShareResponse;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.repository.ShareRepository;
import vn.iostar.postservice.service.LikeService;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.ShareService;
import vn.iostar.postservice.service.client.UserClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final UserClientService userClientService;
    private final JwtService jwtService;
    private final ShareService shareService;
    private final ShareRepository shareRepository;

    @Override
    public void delete(Like entity) {
        likeRepository.delete(entity);
    }

    @Override
    public <S extends Like> S save(S entity) {
        return likeRepository.save(entity);
    }

    @Override
    public Optional<Like> findById(String id) {
        return likeRepository.findById(id);
    }

    @Override
    public ResponseEntity<GenericResponse> getLikeOfPost(String postId) {
        Optional<Post> post = postService.findById(postId);
        if (post.isEmpty())
            throw new RuntimeException("Post not found");
        UserProfileResponse userProfileResponse = userClientService.getUser(post.get().getUserId());
        List<Like> likes = likeRepository.findByPostId(postId);
        if (likes.isEmpty())
            throw new RuntimeException("This post has no like");
        List<LikePostResponse> likePostResponses = new ArrayList<>();
        for (Like like : likes) {
            likePostResponses.add(new LikePostResponse(like,userProfileResponse));
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving like of post successfully")
                .result(likePostResponses).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> getCountLikeOfPost(String postId) {
        Optional<Post> post = postService.findById(postId);
        if (post.isEmpty())
            throw new RuntimeException("Post not found");
        UserProfileResponse userProfileResponse = userClientService.getUser(post.get().getUserId());
        List<Like> likes = likeRepository.findByPostId(postId);
        if (likes.isEmpty())
            throw new RuntimeException("This post has no like");
        List<LikePostResponse> likePostResponses = new ArrayList<>();
        for (Like like : likes) {
            likePostResponses.add(new LikePostResponse(like,userProfileResponse));
        }
        return ResponseEntity
                .ok(GenericResponse.builder().success(true).message("Retrieving number of likes of Post successfully")
                        .result(likePostResponses.size()).statusCode(HttpStatus.OK.value()).build());
    }

    public Optional<Like> findByPostAndUser(Post post, UserProfileResponse user) {
        return likeRepository.findByPostAndUserId(post, user.getUserId());
    }

    @Override
    public ResponseEntity<Object> toggleLikePost(String token, String postId) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Post> post = postService.findById(postId);
        if (!post.isPresent()) {
            return ResponseEntity.badRequest().body("Post not found");
        }
        // Kiểm tra xem cặp giá trị postId và userId đã tồn tại trong bảng Like chưa
        Optional<Like> existingLike = findByPostAndUser(post.get(), user);


        if (existingLike.isPresent()) {
            // Lấy post của like
            Post postLike = existingLike.get().getPost();
            // Nếu đã tồn tại, thực hiện xóa
            Like like = existingLike.get();
            delete(like);

            if (postLike != null) {
                // Lấy danh sách likes của post
                List<String> postLikes = postLike.getLikes();

                if (postLikes != null) {
                    // Kiểm tra xem danh sách likes của post có null hay không
                    // Nếu là null, khởi tạo một danh sách mới
                    if (postLikes == null) {
                        postLikes = new ArrayList<>();
                    }

                    // Xóa commentId khỏi danh sách comments của post
                    postLikes.remove(existingLike.get().getId());

                    // Cập nhật lại danh sách comments của post
                    postLike.setLikes(postLikes);

                    // Cập nhật lại post vào MongoDB
                    postRepository.save(postLike);
                }
            }
            return ResponseEntity.ok("Like post removed successfully");
        } else {
            // Nếu chưa tồn tại, tạo và lưu Like mới
            String likeId = UUID.randomUUID().toString();
            Like like = new Like();
            like.setId(likeId);
            like.setPost(post.get());
            like.setUserId(user.getUserId());
            like.setStatus(null); // Cập nhật status nếu cần
            save(like);

            // Thêm commentId mới vào danh sách likes của post
            List<String> postLikes = post.get().getLikes();
            if (postLikes == null) {
                postLikes = new ArrayList<>();
            }
            postLikes.add(like.getId());
            post.get().setLikes(postLikes);

            // Cập nhật lại post vào MongoDB
            postRepository.save(post.get());

            GenericResponse response = GenericResponse.builder().success(true).message("Like Post Successfully").result(
                            new LikePostResponse(like.getId(), like.getPost().getId(), user.getUserName()))
                    .statusCode(200).build();

            return ResponseEntity.ok(response);
        }
    }


    @Override
    public ResponseEntity<Object> checkUserLikePost(String token, String postId) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Post> post = postService.findById(postId);
        if (!post.isPresent()) {
            return ResponseEntity.badRequest().body("Post not found");
        }
        // Kiểm tra xem cặp giá trị postId và userId đã tồn tại trong bảng Like chưa
        Optional<Like> existingLike = findByPostAndUser(post.get(), user);

        if (existingLike.isPresent()) {
            // Nếu đã tồn tại, trả về true
            GenericResponse response = GenericResponse.builder().success(true).message("Is Liked").result(true)
                    .statusCode(200).build();
            return ResponseEntity.ok(response);
        } else {
            GenericResponse response = GenericResponse.builder().success(true).message("Not Like").result(false)
                    .statusCode(200).build();
            return ResponseEntity.ok(response);
        }
    }

    @Override
    public ResponseEntity<Object> listUserLikePost(String postId) {
        List<ListUserLikePost> listUser = likeRepository.findUsersLikedPost(postId);

        for (ListUserLikePost userLikePost : listUser) {
            // Lấy thông tin user từ userId
            UserProfileResponse user = userClientService.getUser(userLikePost.getUserId());
            if (user != null) {
                // Gán tên và avatar của user vào ListUserLikePost
                userLikePost.setUserName(user.getUserName());
                userLikePost.setAvatar(user.getAvatar());
            }
        }

        GenericResponse response = GenericResponse.builder()
                .success(true)
                .message("List User Like Post")
                .result(listUser)
                .statusCode(200)
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GenericResponse> getLikeOfShare(String shareId) {
        Optional<Share> share = shareService.findById(shareId);
        if (share.isEmpty())
            throw new RuntimeException("Share not found");
        List<Like> likes = likeRepository.findByShareId(shareId);
        UserProfileResponse user = userClientService.getUser(share.get().getUserId());
        if (likes.isEmpty())
            throw new RuntimeException("This share has no like");
        List<LikeShareResponse> likeShareResponses = new ArrayList<>();
        for (Like like : likes) {
            likeShareResponses.add(new LikeShareResponse(like,user));
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving like of post successfully")
                .result(likeShareResponses).statusCode(HttpStatus.OK.value()).build());
    }

    public Optional<Like> findByShareAndUser(Share share, UserProfileResponse user) {
        return likeRepository.findByShareAndUserId(share, user.getUserId());
    }

    @Override
    public ResponseEntity<Object> toggleLikeShare(String token, String shareId) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Share> share = shareService.findById(shareId);
        if (!share.isPresent()) {
            return ResponseEntity.badRequest().body("Share not found");
        }
        // Kiểm tra xem cặp giá trị postId và userId đã tồn tại trong bảng Like chưa
        Optional<Like> existingLike = findByShareAndUser(share.get(), user);

        if (existingLike.isPresent()) {
            // Lấy post của like
            Share shareLike = existingLike.get().getShare();
            // Nếu đã tồn tại, thực hiện xóa
            delete(existingLike.get());
            if (shareLike != null) {
                // Lấy danh sách likes của share
                List<String> shareLikes = shareLike.getLikes();

                if (shareLikes != null) {
                    // Kiểm tra xem danh sách likes của share có null hay không
                    // Nếu là null, khởi tạo một danh sách mới
                    if (shareLikes == null) {
                        shareLikes = new ArrayList<>();
                    }

                    // Xóa commentId khỏi danh sách comments của post
                    shareLikes.remove(existingLike.get().getId());

                    // Cập nhật lại danh sách comments của post
                    shareLike.setLikes(shareLikes);

                    // Cập nhật lại post vào MongoDB
                    shareRepository.save(shareLike);
                }
            }
            return ResponseEntity.ok("Like share post removed successfully");
        } else {
            // Nếu chưa tồn tại, tạo và lưu Like mới
            String likeId = UUID.randomUUID().toString();
            Like like = new Like();
            like.setId(likeId);
            like.setShare(share.get());
            like.setUserId(user.getUserId());
            like.setStatus(null); // Cập nhật status nếu cần
            save(like);

            GenericResponse response = GenericResponse.builder().success(true).message("Like Post Successfully").result(
                            new LikePostResponse(like.getId(), like.getShare().getId(), user.getUserName()))
                    .statusCode(200).build();

            return ResponseEntity.ok(response);
        }
    }

    @Override
    public ResponseEntity<Object> checkUserLikeShare(String token, String shareId) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Share> share = shareService.findById(shareId);
        if (!share.isPresent()) {
            return ResponseEntity.badRequest().body("Share not found");
        }
        // Kiểm tra xem cặp giá trị postId và userId đã tồn tại trong bảng Like chưa
        Optional<Like> existingLike = findByShareAndUser(share.get(), user);

        if (existingLike.isPresent()) {
            // Nếu đã tồn tại, trả về true
            GenericResponse response = GenericResponse.builder().success(true).message("Is Liked").result(true)
                    .statusCode(200).build();
            return ResponseEntity.ok(response);
        } else {
            GenericResponse response = GenericResponse.builder().success(true).message("Not Like").result(false)
                    .statusCode(200).build();
            return ResponseEntity.ok(response);
        }
    }

    @Override
    public ResponseEntity<Object> listUserLikeShare(String shareId) {
        List<ListUserLikePost> listUser = likeRepository.findUsersLikedShare(shareId);
        GenericResponse response = GenericResponse.builder().success(true).message("List User Like Share").result(listUser)
                .statusCode(200).build();
        return ResponseEntity.ok(response);
    }

}
