package vn.iostar.friendservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.friendservice.entity.FriendRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {

    List<FriendRequest> findAllByRecipientId(String recipientId);
    Page<FriendRequest> findAllByRecipientId(String recipientId, Pageable pageable);

    @Query("{'$or': [{'senderId': ?0, 'recipientId': ?1}, {'senderId': ?1, 'recipientId': ?0}]}")
    Optional<FriendRequest> findByTwoUserId(String senderId, String recipientId);

    //findAllBySenderIdPageable
    List<FriendRequest> findAllBySenderId(String senderId);
    Page<FriendRequest> findAllBySenderId(String senderId, Pageable pageable);

    Optional<FriendRequest> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
