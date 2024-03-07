package vn.iostar.friendservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.friendservice.dto.FriendshipDto;
import vn.iostar.friendservice.dto.response.FriendOfUserResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;

import java.util.List;

public interface FriendService {
    ResponseEntity<List<String>> getFriendIds(String userId);

    ResponseEntity<GenericResponse> deleteFriend(String userId, String friendId);

    ResponseEntity<String> createFriendship(String userId);

    ResponseEntity<GenericResponse> validateFriendship(String userId, String friendId);

    ResponseEntity<List<FriendOfUserResponse>> getFriendsOfUser(String userId, String friendId);

	ResponseEntity<List<String>> getFriendSuggestions(String userId);

    ResponseEntity<GenericResponse> createFriend(FriendshipDto friend);
}
