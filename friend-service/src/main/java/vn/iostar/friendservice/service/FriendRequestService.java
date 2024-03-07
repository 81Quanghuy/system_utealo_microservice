package vn.iostar.friendservice.service;


import org.springframework.http.ResponseEntity;
import vn.iostar.friendservice.dto.FriendRequestDto;
import vn.iostar.friendservice.dto.request.CreateFriendRequest;
import vn.iostar.friendservice.dto.response.GenericResponse;

import java.util.List;

public interface FriendRequestService {
    ResponseEntity<List<FriendRequestDto>> getFriendRequests(String userId);

    ResponseEntity<GenericResponse> createFriendRequest(String userId, CreateFriendRequest createFriendRequest);

    ResponseEntity<GenericResponse> acceptFriendRequest(String userId, String friendRequestId);

    ResponseEntity<GenericResponse> rejectFriendRequest(String userId, String friendRequestId);

    ResponseEntity<GenericResponse> deleteFriendRequest(String userId, String friendRequestId);
}
