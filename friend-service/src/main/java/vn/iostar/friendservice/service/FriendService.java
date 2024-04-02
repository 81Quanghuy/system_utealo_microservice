package vn.iostar.friendservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.friendservice.dto.FriendshipDto;
import vn.iostar.friendservice.dto.response.FriendOfUserResponse;
import vn.iostar.friendservice.dto.response.FriendResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.entity.Friend;

import java.util.List;
import java.util.Optional;

public interface FriendService {
    ResponseEntity<List<String>> getFriendIds(String userId);

    ResponseEntity<GenericResponse> deleteFriend(String userId, String friendId);

    ResponseEntity<String> createFriendship(String userId);

    ResponseEntity<GenericResponse> validateFriendship(String userId, String friendId);

    ResponseEntity<List<FriendOfUserResponse>> getFriendsOfUser(String userId, String friendId);

	ResponseEntity<GenericResponse> getFriendSuggestions(String userId);

    ResponseEntity<GenericResponse> createFriend(FriendshipDto friend);

    //findFriendUserIdsByUserId
    Optional<Friend> findByUserId(String userId);

    List<FriendResponse> findFriendUserIdsByUserId(String userId);

    ResponseEntity<GenericResponse> findFriendSuggestions(String userIdToken);
}
