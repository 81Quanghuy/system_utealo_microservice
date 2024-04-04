package vn.iostar.postservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.dto.response.PhoToResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Post;

import java.util.Collection;
import java.util.Date;
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

    Page<Post> findAllByOrderByPostTimeDesc(Pageable pageable);
    // Lấy những bài post trong khoảng thời gian
    List<Post> findByPostTimeBetween(Date startDate, Date endDate);
    // Đếm số lượng bài post trong khoảng thời gian
    long countByPostTimeBetween(Date startDate, Date endDate);

    // Đếm số lượng bài post trong khoảng thời gian 1 tháng
//    @Query("SELECT COUNT(p) FROM Post p WHERE p.postTime BETWEEN ?1 AND ?2")
//    long countPostsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
//    long countPostBetweenStartDateAndEndDate(Date startDate, Date endDate);

    // Đếm số lượng bài post của người dùng trong 1 tháng
    long countByUserIdAndPostTimeBetween(String userId, Date start, Date end);
    // Định nghĩa phương thức để tìm tất cả bài post của một userId và sắp xếp theo thời gian đăng bài giảm dần
    Page<Post> findAllByUserIdOrderByPostTimeDesc(String userId, Pageable pageable);
    // Đếm số lượng bài post của một người dùng cụ thể
    Long countPostsByUserId(String userId);
    // Định nghĩa hàm lấy những bài post của 1 user trong 1 tháng
    Page<Post> findByUserIdAndPostTimeBetween(String userId, Date start, Date end, Pageable pageable);
    


}
