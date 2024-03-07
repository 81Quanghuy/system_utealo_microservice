package vn.iostar.conversationservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;
import vn.iostar.conversationservice.entity.ChatGroup;

@Repository
public interface ChatGroupRepository  extends MongoRepository<ChatGroup, String> {
}
