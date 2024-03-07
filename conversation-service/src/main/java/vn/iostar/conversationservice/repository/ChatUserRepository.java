package vn.iostar.conversationservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;
import vn.iostar.conversationservice.entity.ChatUser;
@Repository
public interface ChatUserRepository  extends MongoRepository<ChatUser, String> {
}
