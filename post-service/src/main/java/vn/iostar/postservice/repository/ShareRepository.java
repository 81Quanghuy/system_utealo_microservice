package vn.iostar.postservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.postservice.entity.Share;

@Repository
public interface ShareRepository extends MongoRepository<Share, String> {
}
