package vn.iostar.postservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.entity.Comment;

import java.util.Date;
import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String>{
    List<Comment> findByPostIdAndCommentReplyIsNullOrderByCreateTimeDesc(String postId);
    List<Comment> findByPostIdOrderByCreateTimeDesc(String postId);
    List<Comment> findCommentRepliesByIdOrderByCreateTimeDesc(String commentId);
    List<Comment> findByCommentReplyIdOrderByCreateTimeDesc(String commentId);
    List<Comment> findByShareIdAndCommentReplyIsNullOrderByCreateTimeDesc(String shareId);
    // Lấy tất cả comment trong hệ thống
    Page<Comment> findAllByOrderByCreateTimeDesc(Pageable pageable);
    // Đếm số lượng comment trong khoảng thời gian
    long countByCreateTimeBetween(Date startDateAsDate, Date endDateAsDate);
    // Lấy những bình luận trong khoảng thời gian
    List<Comment> findByCreateTimeBetween(Date startDate, Date endDate);
    // Định nghĩa phương thức để tìm tất cả bình luận của một userId và sắp xếp theo
    // thời gian đăng bài giảm dần
    Page<Comment> findAllByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);
    // Đếm số lượng bình luận của một người dùng cụ thể
    Long countCommentsByUserId(String userId);
    // Đếm số lượng bình luận của người dùng trong 1 tháng
    long countByUserIdAndCreateTimeBetween(String userId, Date start, Date end);

}
