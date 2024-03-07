package vn.iostar.friendservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iostar.friendservice.dto.FriendshipDto;
import vn.iostar.friendservice.dto.response.FriendOfUserResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.friendservice.entity.FriendRequest;
import vn.iostar.friendservice.exception.wrapper.BadRequestException;
import vn.iostar.friendservice.exception.wrapper.NotFoundException;
import vn.iostar.friendservice.repository.FriendRepository;
import vn.iostar.friendservice.repository.FriendRequestRepository;
import vn.iostar.friendservice.service.FriendService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Override
    public ResponseEntity<List<String>> getFriendIds(String userId) {
        log.info("FriendshipServiceImpl, getFriendIds");
        Optional<Friend> friendship = friendshipRepository.findByAuthorId(userId);
        return friendship.map(value -> ResponseEntity.ok(value.getFriendIds()))
                .orElseGet(() -> ResponseEntity.ok(List.of()));
    }

    @Override
    public ResponseEntity<GenericResponse> deleteFriend(String userId, String friendId) {
        log.info("FriendshipServiceImpl, deleteFriend");
        Optional<Friend> optionalSenderRecipient = friendshipRepository.findByAuthorId(userId);
        Optional<Friend> optionalRecipientSender = friendshipRepository.findByAuthorId(friendId);
        if (optionalSenderRecipient.isPresent() && optionalRecipientSender.isPresent()) {
            Friend senderRecipient = optionalSenderRecipient.get();
            Friend recipientSender = optionalRecipientSender.get();
            senderRecipient.getFriendIds().remove(friendId);
            recipientSender.getFriendIds().remove(userId);
            friendshipRepository.saveAll(List.of(senderRecipient, recipientSender));
            Optional<FriendRequest> optionalFriendRequest = friendRequestRepository.findByTwoUserId(userId, friendId);
            optionalFriendRequest.ifPresent(friendRequestRepository::delete);
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Xoá bạn thành công!")
                    .result(null)
                    .build());
        }
        throw new BadRequestException("Bạn không có quyền xoá bạn bè này!");
    }

    @Override
    public ResponseEntity<String> createFriendship(String userId) {
        log.info("FriendshipServiceImpl, createFriendship");
        Optional<Friend> optionalFriendship = friendshipRepository.findByAuthorId(userId);
        if (optionalFriendship.isPresent()) {
            return ResponseEntity.ok("Đã tồn tại mối quan hệ bạn bè!");
        }
        Friend friendship = Friend.builder()
                .id(UUID.randomUUID().toString())
                .authorId(userId)
                .friendIds(List.of())
                .createdAt(new Date())
                .build();
        friendshipRepository.save(friendship);
        return ResponseEntity.ok("Tạo mối quan hệ bạn bè thành công!");
    }

    @Override
    public ResponseEntity<GenericResponse> validateFriendship(String userId, String friendId) {
        log.info("FriendshipServiceImpl, validateFriendship");
        Friend senderRecipient = friendshipRepository.findByAuthorId(userId)
                .orElseThrow(() -> new BadRequestException("Bạn không có quyền thực hiện hành động này!"));
        if (senderRecipient.getFriendIds().contains(friendId)) {
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Bạn bè")
                    .result(true)
                    .build());
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Không phải bạn bè")
                .result(false)
                .build());
    }

    @Override
    public ResponseEntity<List<FriendOfUserResponse>> getFriendsOfUser(String userId, String friendId) {
        log.info("FriendshipServiceImpl, getFriendsOfUser");
        Friend friendship = friendshipRepository.findByAuthorId(friendId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng này!"));
        Friend user = friendshipRepository.findByAuthorId(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng này!"));
        List<FriendOfUserResponse> friendOfUserResponses = friendship
                .getFriendIds()
                .stream()
                .map(uId -> FriendOfUserResponse.builder()
                        .userId(uId)
                        .isFriendOfMe(user.getFriendIds().contains(uId))
                        .build())
                .toList();
        return ResponseEntity.ok(friendOfUserResponses);
    }

	@Override
	public ResponseEntity<List<String>> getFriendSuggestions(String userId) {
		log.info("FriendshipServiceImpl, getFriendSuggestions");
		
		Friend friendship = friendshipRepository.findByAuthorId(userId)
				.orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng này!"));
		
		List<String> friendIds = friendship.getFriendIds();
		
		List<String> userIdResult = new ArrayList<>();
		
		while (userIdResult.size() < 10 && !friendIds.isEmpty()) {
			String friendId = friendIds.remove(0);
			Friend friendFriendship = friendshipRepository.findByAuthorId(friendId)
					.orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng này!"));
			List<String> friendFriendIds = friendFriendship.getFriendIds();
			for (String friendFriendId : friendFriendIds) {
				if (!friendIds.contains(friendFriendId) && !friendFriendId.equals(userId)) {
					userIdResult.add(friendFriendId);
				}
			}
		}
		
		return ResponseEntity.ok(userIdResult);
		
	}

    @Override
    public ResponseEntity<GenericResponse> createFriend(FriendshipDto friend) {
        //create friend from friendshipdto
        log.info("FriendshipServiceImpl, createFriend");
        Friend friendship = Friend.builder()
                .id(UUID.randomUUID().toString())
                .authorId(friend.getAuthorId())
                .friendIds(friend.getFriendIds())
                .createdAt(new Date())
                .build();
        friendshipRepository.save(friendship);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Tạo bạn bè thành công!")
                .result(null)
                .build());

    }
}
