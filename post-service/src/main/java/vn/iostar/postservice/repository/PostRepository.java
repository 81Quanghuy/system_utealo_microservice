package vn.iostar.postservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.entity.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

}
