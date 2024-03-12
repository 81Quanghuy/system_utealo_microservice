package vn.iostar.friendservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.friendservice.dto.FriendDTO;
import vn.iostar.friendservice.entity.Friend;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends MongoRepository<Friend, String> {

    Optional<Friend> findByAuthorIdAndFriendIdsContaining(String authorId, String userId);

    Optional<Friend> findByAuthorId(String authorId);

    // Đề xuất bạn của bạn bè với số lượng giới hạn là 10 người sử dụng userId

    List<FriendDTO> findFriendSuggestions(String userId);
}
