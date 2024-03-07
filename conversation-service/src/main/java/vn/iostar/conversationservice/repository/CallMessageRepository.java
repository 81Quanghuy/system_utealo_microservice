package vn.iostar.conversationservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.conversationservice.entity.CallMessage;

@Repository
public interface CallMessageRepository extends MongoRepository<CallMessage, String> {
}
