package vn.iostar.postservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.dto.response.ListUserLikePost;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.entity.Like;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.entity.Share;

import java.util.List;
import java.util.Optional;


@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    List<Like> findByPostId(String postId);
    Optional<Like> findByPostAndUserId(Post post, String userId);
//    @Query("SELECT NEW vn.iostar.dto.ListUserLikePost(l.user.userName, l.user.userId, l.user.profile.avatar) FROM Like l WHERE l.post.postId = ?1")
//    List<ListUserLikePost> findUsersLikedPost(@Param("postId") String postId);
    List<Like> findByShareId(String shareId);
    Optional<Like> findByShareAndUserId(Share share, String userId);

//    @Query("SELECT NEW vn.iostar.dto.ListUserLikePost(l.user.userName, l.user.userId, l.user.profile.avatar) FROM Like l WHERE l.share.shareId = ?1")
//    List<ListUserLikePost> findUsersLikedShare(@Param("shareId") String shareId);
    List<Like> findByCommentId(String commentId);
    Optional<Like> findByCommentAndUserId(Comment comment, String userId);

    List<Like> findLikeIdsByCommentId(String commentId);
    List<Like> findLikeIdsByPostId(String postId);
    List<Like> findLikeIdsByShareId(String shareId);

}
