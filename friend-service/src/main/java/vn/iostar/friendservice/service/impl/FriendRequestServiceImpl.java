package vn.iostar.friendservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iostar.friendservice.constant.FriendStateEnum;
import vn.iostar.friendservice.dto.FriendRequestDto;
import vn.iostar.friendservice.dto.request.CreateFriendRequest;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.friendservice.entity.FriendRequest;
import vn.iostar.friendservice.exception.wrapper.BadRequestException;
import vn.iostar.friendservice.exception.wrapper.NotFoundException;
import vn.iostar.friendservice.repository.FriendRepository;
import vn.iostar.friendservice.repository.FriendRequestRepository;
import vn.iostar.friendservice.service.FriendRequestService;
import vn.iostar.friendservice.service.MapperService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;
    private final MapperService mapperService;

    @Override
    public ResponseEntity<List<FriendRequestDto>> getFriendRequests(String userId) {
        log.info("FriendRequestServiceImpl, getFriendRequests");
        List<FriendRequest> friendRequests = friendRequestRepository.findAllByRecipientId(userId);
        List<FriendRequestDto> friendRequestDtos = friendRequests.stream()
                .map(mapperService::mapToFriendRequestDto)
                .toList();
        return ResponseEntity.ok(friendRequestDtos);
    }

    @Override
    public ResponseEntity<GenericResponse> createFriendRequest(String userId, CreateFriendRequest createFriendRequest) {
        log.info("FriendRequestServiceImpl, createFriendRequest");
        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findByTwoUserId(userId, createFriendRequest.getUserId());
        if (optionalFriendRequest.isPresent()) {
            if (optionalFriendRequest.get().getState().equals(FriendStateEnum.REJECTED)) {
                FriendRequest friendRequest = optionalFriendRequest.get();
                friendRequest.setState(FriendStateEnum.PENDING);
                friendRequest.setUpdatedAt(new Date());
                friendRequestRepository.save(friendRequest);
                return ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Gửi lại lời mời kết bạn thành công!")
                        .result(mapperService.mapToFriendRequestDto(friendRequest))
                        .build());
            } else if (optionalFriendRequest.get().getState().equals(FriendStateEnum.PENDING)) {
                throw new BadRequestException("Lời mời kết bạn đã được gửi trước đó!");
            } else {
                throw new BadRequestException("Hai người đã là bạn bè!");
            }
        }
        if (friendshipRepository.findByAuthorIdAndFriendIdsContaining(userId, createFriendRequest.getUserId()).isPresent()) {
            throw new BadRequestException("Hai người đã là bạn bè!");
        }
        FriendRequest friendRequest = friendRequestRepository.save(FriendRequest.builder()
                .id(UUID.randomUUID().toString())
                .senderId(userId)
                .recipientId(createFriendRequest.getUserId())
                .state(FriendStateEnum.PENDING)
                .createdAt(new Date())
                .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Gửi lời mời kết bạn thành công!")
                .result(mapperService.mapToFriendRequestDto(friendRequest))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> acceptFriendRequest(String userId, String friendRequestId) {
        log.info("FriendRequestServiceImpl, acceptFriendRequest");
        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findById(friendRequestId);
        FriendRequest friendRequest = getFriendRequest(optionalFriendRequest, userId, FriendStateEnum.ACCEPTED);
        friendRequest = friendRequestRepository.save(friendRequest);
        Friend senderRecipient = friendshipRepository.findByAuthorId(userId)
                        .orElseThrow(() -> new NotFoundException("Yêu cầu không phù hợp!"));
        if (senderRecipient.getFriendIds().contains(friendRequest.getSenderId())) {
            throw new BadRequestException("Hai người đã là bạn bè!");
        }
        Friend recipientSender = friendshipRepository.findByAuthorId(friendRequest.getSenderId())
                .orElseThrow(() -> new NotFoundException("Yêu cầu không phù hợp!"));
        senderRecipient.getFriendIds().add(friendRequest.getSenderId());
        recipientSender.getFriendIds().add(userId);
        friendshipRepository.save(senderRecipient);
        friendshipRepository.save(recipientSender);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Chấp nhận lời mời kết bạn thành công!")
                .result(mapperService.mapToFriendRequestDto(friendRequest))
                .build());

    }

    @Override
    public ResponseEntity<GenericResponse> rejectFriendRequest(String userId, String friendRequestId) {
        log.info("FriendRequestServiceImpl, rejectFriendRequest");
        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findById(friendRequestId);
        FriendRequest friendRequest = getFriendRequest(optionalFriendRequest, userId, FriendStateEnum.REJECTED);
        friendRequest = friendRequestRepository.save(friendRequest);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Từ chối lời mời kết bạn thành công!")
                .result(mapperService.mapToFriendRequestDto(friendRequest))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteFriendRequest(String userId, String friendRequestId) {
        log.info("FriendRequestServiceImpl, deleteFriendRequest");
        Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findById(friendRequestId);
        if (optionalFriendRequest.isEmpty()) {
            throw new BadRequestException("Không tìm thấy lời mời kết bạn!");
        }
        FriendRequest friendRequest = optionalFriendRequest.get();
        if (!friendRequest.getSenderId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền thực hiện hành động này!");
        }
        friendRequestRepository.delete(friendRequest);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Xóa lời mời kết bạn thành công!")
                .result(mapperService.mapToFriendRequestDto(friendRequest))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getStatusByUserId(String userId, String userIdToken) {
        Optional<Friend> friend = friendRepository.findByAuthorIdAndFriendIdsContaining(userId, userIdToken);

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

    private static FriendRequest getFriendRequest(Optional<FriendRequest> optionalFriendRequest, String userId, FriendStateEnum rejected) {
        if (optionalFriendRequest.isEmpty()) {
            throw new BadRequestException("Không tìm thấy lời mời kết bạn!");
        }
        FriendRequest friendRequest = optionalFriendRequest.get();
        if (!friendRequest.getRecipientId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền thực hiện hành động này!");
        }
        if (friendRequest.getState().equals(FriendStateEnum.ACCEPTED)) {
            throw new BadRequestException("Lời mời kết bạn đã được chấp nhận trước đó!");
        }
        if (friendRequest.getState().equals(FriendStateEnum.REJECTED)) {
            throw new BadRequestException("Lời mời kết bạn đã bị từ chối trước đó!");
        }
        friendRequest.setState(rejected);
        friendRequest.setUpdatedAt(new Date());
        return friendRequest;
    }
}
