package vn.iostar.postservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.dto.response.PhoToResponse;
import vn.iostar.postservice.entity.Post;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUserIdAndPrivacyLevelInOrderByPostTimeDesc(String userId,
                                                                Collection<PrivacyLevel> privacyLevel, Pageable pageable);

    @Query("SELECT p.photos FROM Post p WHERE p.userId = ?1 ORDER BY p.postTime DESC")
    List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId);


    @Query("SELECT NEW vn.iostar.dto.PhoToResponse(p.postId, p.photos) " +
            "FROM Post p " +
            "WHERE p.userId = ?1 " +
            "AND p.photos IS NOT NULL " +
            "AND p.photos <> '' " +
            "AND p.privacyLevel NOT IN ?2 " +
            "ORDER BY p.postTime DESC")
    List<PhoToResponse> findLatestPhotosByUserIdAndNotNull(
            @Param("privacyLevels") List<PrivacyLevel> privacyLevels,
            String userId,
            Pageable pageable);


}
