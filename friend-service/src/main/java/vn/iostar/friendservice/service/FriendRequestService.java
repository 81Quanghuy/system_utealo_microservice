package vn.iostar.friendservice.service;


import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import vn.iostar.friendservice.dto.FriendRequestDto;
import vn.iostar.friendservice.dto.request.CreateFriendRequest;
import vn.iostar.friendservice.dto.response.GenericResponse;

import java.util.List;

public interface FriendRequestService {


    ResponseEntity<GenericResponse> deleteFriendRequest(String userId, String friendRequestId);

    ResponseEntity<GenericResponse> getStatusByUserId(String userId, String userIdToken);

    ResponseEntity<GenericResponse> getRequestList(String userId);

    ResponseEntity<GenericResponse> getSenderRequestPageable(String userId);

    ResponseEntity<GenericResponse> sendFriendRequest(String userId, String userIdToken);

    ResponseEntity<GenericResponse> getInvitationSenderList(String userId);

    ResponseEntity<GenericResponse> cancelRequestFriend(String userIdToken, String userId);

    ResponseEntity<GenericResponse> acceptRequest(String userIdToken, String userId);

    ResponseEntity<GenericResponse> getInvitationSenderListPageable(String userId, Pageable pageable);
}
