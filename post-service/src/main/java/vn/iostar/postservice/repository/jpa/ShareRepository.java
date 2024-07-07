package vn.iostar.postservice.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.entity.Post;
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
    List<Share> findByUserId(String userId, Pageable pageable);
    //  Lấy những bài share của mình privacyLevel khác PRIVATE, bạn bè của mình với privacyLevel khác PRIVATE, những nhóm mà mình tham gia có privacyLevel là GROUP_MEMBERS, chỉ truyền vào tham số là danh sách id và pageable
    @Query("{$and:[" +
            "{$or:[{'userId': {$in: ?0}}, {'groupId': {$in: ?1}}]}," +
            "{'$or':[{'privacyLevel': {$ne: 'PRIVATE'}}, {'privacyLevel': 'GROUP_MEMBERS'}]}" +
            "]}"
    )
    List<Share> findSharePostsInTimeLine(List<String> userIds, List<String> groupIds, Pageable pageable);
    // Lấy tất cả các bài share của những nhóm mình tham gia
    @Query("{$and:[{$or:[{'postGroupId': {$in: ?0}}]}]}")
    List<Share> findAllSharesInUserGroups(List<String> groupIds, Pageable pageable);

    // Lấy những bài share post của nhóm
    List<Share> findByPostGroupIdOrderByCreateAtDesc(String postGroupId, Pageable pageable);

    // Xóa bài share theo postId
    void deleteByPostId(String postId);

}
