package vn.iostar.postservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.entity.Share;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface ShareRepository extends MongoRepository<Share, String> {
    List<Share> findByUserIdAndPrivacyLevelInOrderByCreateAtDesc(String userId,
                                                                 Collection<PrivacyLevel> privacyLevel, Pageable pageable);
    // Lấy tất cả bài post trong hệ thống
    Page<Share> findAllByOrderByCreateAtDesc(Pageable pageable);
    // Lấy những bài share post trong khoảng thời gian
    List<Share> findByCreateAtBetween(Date startDate, Date endDate);
    long countByCreateAtBetween(Date startDate, Date endDate);
    // Đếm số lượng bài share của người dùng trong 1 tháng
    long countByUserIdAndCreateAtBetween(String userId, Date start, Date end);
    // Tìm tất cả bài post của 1 user trong 1 tháng
    Page<Share> findByUserIdAndCreateAtBetween(String userId, Date start, Date end, Pageable pageable);
    // Định nghĩa phương thức để tìm tất cả bài share của một userId và sắp xếp theo
    // thời gian đăng bài giảm dần
    Page<Share> findAllByUserIdOrderByCreateAtDesc(String userId, Pageable pageable);
    // Đếm số lượng bài share của một người dùng cụ thể
    Long countSharesByUserId(String userId);
    // Tìm tất cả bài share của một người dùng cụ thể
    List<Share> findByUserUserId(String userId, Pageable pageable);
}
