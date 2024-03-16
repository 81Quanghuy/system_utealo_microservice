package vn.iostar.friendservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iostar.friendservice.dto.UserIds;
import vn.iostar.friendservice.dto.response.FriendResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.friendservice.entity.FriendRequest;
import vn.iostar.friendservice.exception.wrapper.BadRequestException;
import vn.iostar.friendservice.exception.wrapper.NotFoundException;
import vn.iostar.friendservice.repository.FriendRepository;
import vn.iostar.friendservice.repository.FriendRequestRepository;
import vn.iostar.friendservice.service.FriendRequestService;
import vn.iostar.friendservice.service.client.UserClientService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final UserClientService userClientService;


    @Override
    public ResponseEntity<GenericResponse> deleteFriendRequest(String userIdToken, String userId) {
        log.info("FriendRequestServiceImpl, deleteFriendRequest");
        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findByTwoUserId(userIdToken, userId);
        if (optionalFriendRequest.isEmpty()) {
            throw new BadRequestException("Không tìm thấy lời mời kết bạn!");
        }
        FriendRequest friendRequest = optionalFriendRequest.get();
        if (friendRequest.getRecipientId().equals(userIdToken) ) {
            friendRequestRepository.delete(friendRequest);
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Xóa lời mời kết bạn thành công!")
                    .build());
        }
        throw new BadRequestException("Không thể xóa lời mời kết bạn!");
    }

    @Override
    public ResponseEntity<GenericResponse> getStatusByUserId(String userId, String userIdToken) {
        Optional<Friend> friend = friendRepository.findByAuthorIdAndFriendIdsContaining(userIdToken, userId);

        // Check if the user is a friend
        if (friend.isPresent()) {
            return ResponseEntity.ok().body(new GenericResponse(true, "Bạn bè", "null", HttpStatus.OK.value()));
        }

        // Check if the user has sent a friend request
        Optional<FriendRequest> friendRequest = friendRequestRepository.findByTwoUserId(userId,
                userIdToken);
        if (friendRequest.isPresent()) {
            if (friendRequest.get().getSenderId().equals(userIdToken)) {
                return ResponseEntity.ok().body(new GenericResponse(true, "Đã gửi lời mời", "null", HttpStatus.OK.value()));
            } else {
                return ResponseEntity.ok().body(new GenericResponse(true, "Chấp nhận lời mời", "null", HttpStatus.OK.value()));
            }
        }
        return ResponseEntity.ok().body(new GenericResponse(true, "Kết bạn", "null", HttpStatus.OK.value()));
    }

    @Override
    public ResponseEntity<GenericResponse> getRequestList(String userId) {
        log.info("FriendRequestServiceImpl, getRequestList");
        List<FriendRequest> friendRequests = friendRequestRepository.findAllBySenderId(userId);
        UserIds userIds = UserIds.builder()
                .userId(friendRequests.stream()
                        .map(FriendRequest::getRecipientId)
                        .toList())
                .build();
        List<FriendResponse> friendResponses = userClientService.getFriendByListUserId(userIds);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách lời mời kết bạn thành công!")
                .result(friendResponses)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getSenderRequestPageable(String userId) {
        log.info("FriendRequestServiceImpl, getSenderRequestPageable");
        PageRequest pageable = PageRequest.of(0, 10);
        Page<FriendRequest> friendRequests = friendRequestRepository.findAllBySenderId(userId, pageable);
        UserIds userIds = UserIds.builder()
                .userId(friendRequests.stream()
                        .map(FriendRequest::getRecipientId)
                        .toList())
                .build();
        List<FriendResponse> friendResponses = userClientService.getFriendByListUserId(userIds);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách lời mời kết bạn thành công!")
                .result(friendResponses)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> sendFriendRequest(String userId, String userIdToken) {
        log.info("FriendRequestServiceImpl, sendFriendRequest");
        Optional<Friend> friend = friendRepository.findByAuthorIdAndFriendIdsContaining(userIdToken, userId);
        if (friend.isPresent()) {
            throw new BadRequestException("Hai người đã là bạn bè!");
        }
        Optional<FriendRequest> friendRequest = friendRequestRepository.findByTwoUserId(userIdToken,userId);
        if (friendRequest.isPresent()) {
            throw new BadRequestException("Đã gửi lời mời kết bạn!");
        }
        friendRequestRepository.save(FriendRequest.builder()
                .id(UUID.randomUUID().toString())
                .senderId(userIdToken)
                .recipientId(userId)
                .isActive(true)
                .createdAt(new Date())
                .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Gửi lời mời kết bạn thành công!")
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getInvitationSenderList(String userId) {
        log.info("FriendRequestServiceImpl, getInvitationSenderList");
        List<FriendRequest> friendRequests = friendRequestRepository.findAllByRecipientId(userId);
        UserIds userIds = UserIds.builder()
                .userId(friendRequests.stream()
                        .map(FriendRequest::getRecipientId)
                        .toList())
                .build();
        List<FriendResponse> friendResponses = userClientService.getFriendByListUserId(userIds);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách lời mời kết bạn thành công!")
                .result(friendResponses)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> cancelRequestFriend(String userIdToken, String userId) {
        log.info("FriendRequestServiceImpl, cancelRequestFriend");
        Optional<FriendRequest> friendRequest = friendRequestRepository.findByTwoUserId(userIdToken,userId);
        if (friendRequest.isEmpty()) {
            throw new NotFoundException("Không tìm thấy lời mời kết bạn!");
        }
        if (!friendRequest.get().getSenderId().equals(userIdToken)) {
            throw new BadRequestException("Không thể hủy lời mời kết bạn!");
        }
        friendRequestRepository.delete(friendRequest.get());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Hủy lời mời kết bạn thành công!")
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> acceptRequest(String userIdToken, String userId) {
        log.info("FriendRequestServiceImpl, acceptRequest");
        Optional<FriendRequest> friendRequest = friendRequestRepository.findByTwoUserId(userId,userIdToken);
        if (friendRequest.isEmpty()) {
            throw new NotFoundException("Không tìm thấy lời mời kết bạn!");
        }
        if (!friendRequest.get().getRecipientId().equals(userIdToken)) {
            throw new BadRequestException("Không thể chấp nhận lời mời kết bạn!");
        }
        friendRequestRepository.delete(friendRequest.get());

        //check if authorId already of userIdToken and userId
        Optional<Friend> friend = friendRepository.findByAuthorId(userIdToken);
        Optional<Friend> friend1 = friendRepository.findByAuthorId(userId);
        if (friend.isPresent()) {
            friend.get().getFriendIds().add(userId);
            friendRepository.save(friend.get());
        } else {
            friendRepository.save(Friend.builder()
                    .id(UUID.randomUUID().toString())
                    .authorId(userIdToken)
                    .friendIds(List.of(userId))
                    .build());
        }
        if (friend1.isPresent()) {
            friend1.get().getFriendIds().add(userIdToken);
            friendRepository.save(friend1.get());
        } else {
            friendRepository.save(Friend.builder()
                    .id(UUID.randomUUID().toString())
                    .authorId(userId)
                    .friendIds(List.of(userIdToken))
                    .build());
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Chấp nhận lời mời kết bạn thành công!")
                .build());
    }

}
