package vn.iostar.postservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.groupservice.dto.FilesOfGroupDTO;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.dto.response.PhoToResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.entity.Share;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    List<Post> findByUserIdAndPrivacyLevelInOrderByPostTimeDesc(String userId,
                                                                Collection<PrivacyLevel> privacyLevel, Pageable pageable);

    @Query(value = "{" +
            "'userId': ?0, " +
            "'photos': {$ne: null, $ne: ''}" +
            "}",
            fields = "{'photos': 1 }")
    List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId);

    // Lấy 9 hình đầu tiên trong bài post của user
    @Query(value = "{" +
            "'userId': ?1, " +
            "'photos': {$ne: null, $ne: ''}," +
            "'privacyLevel': {$nin: ?0}" +
            "}",
            fields = "{ 'postId': 1, 'photos': 1 }")
    List<String> findLatestPhotosByUserIdAndNotNull(
            List<PrivacyLevel> privacyLevels,
            String userId,
            Pageable pageable);

    Page<Post> findAllByOrderByPostTimeDesc(Pageable pageable);

    // Lấy những bài post trong khoảng thời gian
    List<Post> findByPostTimeBetween(Date startDate, Date endDate);

    // Đếm số lượng bài post trong khoảng thời gian
    long countByPostTimeBetween(Date startDate, Date endDate);

    // Đếm số lượng bài post của người dùng trong 1 tháng
    long countByUserIdAndPostTimeBetween(String userId, Date start, Date end);

    // Định nghĩa phương thức để tìm tất cả bài post của một userId và sắp xếp theo thời gian đăng bài giảm dần
    Page<Post> findAllByUserIdOrderByPostTimeDesc(String userId, Pageable pageable);

    // Đếm số lượng bài post của một người dùng cụ thể
    Long countPostsByUserId(String userId);

    // Định nghĩa hàm lấy những bài post của 1 user trong 1 tháng
    Page<Post> findByUserIdAndPostTimeBetween(String userId, Date start, Date end, Pageable pageable);

    //  Lấy những bài post của mình privacyLevel khác PRIVATE, bạn bè của mình với privacyLevel khác PRIVATE, những nhóm mà mình tham gia có privacyLevel là GROUP_MEMBERS, chỉ truyền vào tham số là danh sách id và pageable
    @Query("{$and:[" +
            "{$or:[{'userId': {$in: ?0}}, {'groupId': {$in: ?1}}]}," +
            "{'$or':[{'privacyLevel': {$ne: 'PRIVATE'}}, {'privacyLevel': 'GROUP_MEMBERS'}]}" +
            "]}"
    )
    List<Post> findPostsInTimeLine(List<String> userIds,List<String> groupIds, Pageable pageable);
    // Lấy những bài post của nhóm
    List<Post> findByGroupIdOrderByPostTimeDesc(String postGroupId, Pageable pageable);
    // Lấy tất cả các bài share của những nhóm mình tham gia
    @Query("{$and:[{$or:[{'groupId': {$in: ?0}}]}]}")
    List<Post> findAllPostsInUserGroups(List<String> groupIds, Pageable pageable);
    // Lấy danh sách file của 1 nhóm
    @Query(value = "{'groupId': ?0, 'files': {$nin: [null, '']}}")
    List<String> findFilesOfPostByGroupId(String groupId);
    // Lấy danh sách photo của 1 nhóm
    @Query(value = "{'groupId': ?0, 'photos': {$nin: [null, '']}}")
    Page<String> findPhotosOfPostByGroupId(String groupId, Pageable pageable);
    // Lấy những bài viết trong nhóm do Admin đăng
    @Query(value = "{'userId': {$in: ?0}}")
    List<Post> findPostsByAdminRoleInGroup(List<String> userIds, Pageable pageable);
}
