package vn.iostar.postservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String>{
    List<Comment> findByPostIdAndCommentReplyIsNullOrderByCreateTimeDesc(String postId);
    List<Comment> findCommentRepliesByIdOrderByCreateTimeDesc(String commentId);
    List<Comment> findByCommentReplyId(String commentId);
}
