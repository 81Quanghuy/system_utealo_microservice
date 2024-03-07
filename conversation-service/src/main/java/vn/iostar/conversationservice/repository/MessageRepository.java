package vn.iostar.conversationservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;
import vn.iostar.conversationservice.entity.Message;

@Repository
public interface MessageRepository  extends MongoRepository<Message, String> {
}
