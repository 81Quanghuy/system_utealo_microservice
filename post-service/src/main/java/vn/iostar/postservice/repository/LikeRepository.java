package vn.iostar.postservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.entity.Like;


@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
}
