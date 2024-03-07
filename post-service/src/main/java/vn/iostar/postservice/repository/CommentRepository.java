package vn.iostar.postservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.entity.Comment;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String>{
}
