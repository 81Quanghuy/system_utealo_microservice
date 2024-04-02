package vn.iostar.friendservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iostar.friendservice.dto.FriendshipDto;
import vn.iostar.friendservice.dto.UserIds;
import vn.iostar.friendservice.dto.response.FriendOfUserResponse;
import vn.iostar.friendservice.dto.response.FriendResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.friendservice.exception.wrapper.BadRequestException;
import vn.iostar.friendservice.exception.wrapper.NotFoundException;
import vn.iostar.friendservice.repository.FriendRepository;
import vn.iostar.friendservice.service.FriendService;
import vn.iostar.friendservice.service.client.UserClientService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendServiceImpl implements FriendService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FriendRepository friendRepository;

    private final UserClientService userClientService;

    @Override
    public ResponseEntity<List<String>> getFriendIds(String userId) {
        log.info("FriendshipServiceImpl, getFriendIds");
        Optional<Friend> friendship = friendRepository.findByAuthorId(userId);
        return friendship.map(value -> ResponseEntity.ok(value.getFriendIds()))
                .orElseGet(() -> ResponseEntity.ok(List.of()));
    }

    @Override
    public ResponseEntity<GenericResponse> deleteFriend(String userId, String friendId) {
        log.info("FriendshipServiceImpl, deleteFriend");
        Optional<Friend> optionalSenderRecipient = friendRepository.findByAuthorId(userId);
        Optional<Friend> optionalRecipientSender = friendRepository.findByAuthorId(friendId);
        if (optionalSenderRecipient.isPresent() && optionalRecipientSender.isPresent()) {
            Friend senderRecipient = optionalSenderRecipient.get();
            Friend recipientSender = optionalRecipientSender.get();
            senderRecipient.getFriendIds().remove(friendId);
            recipientSender.getFriendIds().remove(userId);
            friendRepository.saveAll(List.of(senderRecipient, recipientSender));
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
        Optional<Friend> optionalFriendship = friendRepository.findByAuthorId(userId);
        if (optionalFriendship.isPresent()) {
            return ResponseEntity.ok("Đã tồn tại mối quan hệ bạn bè!");
        }
        Friend friendship = Friend.builder()
                .id(UUID.randomUUID().toString())
                .authorId(userId)
                .friendIds(List.of())
                .createdAt(new Date())
                .build();
        friendRepository.save(friendship);
        return ResponseEntity.ok("Tạo mối quan hệ bạn bè thành công!");
    }

    @Override
    public ResponseEntity<GenericResponse> validateFriendship(String userId, String friendId) {
        log.info("FriendshipServiceImpl, validateFriendship");
        Friend senderRecipient = friendRepository.findByAuthorId(userId)
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
        Friend friendship = friendRepository.findByAuthorId(friendId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng này!"));
        Friend user = friendRepository.findByAuthorId(userId)
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
    public ResponseEntity<GenericResponse> getFriendSuggestions(String userId) {
        log.info("FriendshipServiceImpl, getFriendSuggestions");

        Friend friendship = friendRepository.findByAuthorId(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng này!"));

        List<String> friendIds = friendship.getFriendIds();

        List<String> userIdResult = new ArrayList<>();

        while (userIdResult.size() < 10 && !friendIds.isEmpty()) {
            String friendId = friendIds.removeFirst();
            Friend friendFriendship = friendRepository.findByAuthorId(friendId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng này!"));
            List<String> friendFriendIds = friendFriendship.getFriendIds();
            for (String friendFriendId : friendFriendIds) {
                if (!friendIds.contains(friendFriendId) && !friendFriendId.equals(userId)) {
                    userIdResult.add(friendFriendId);
                }
            }
        }
        UserIds userIds = UserIds.builder()
                .userId(userIdResult)
                .build();
        List<FriendResponse> friendResponses = userClientService.getFriendByListUserId(userIds);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get Suggestion List Successfully!")
                .result(friendResponses).statusCode(HttpStatus.OK.value()).build());

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
        friendRepository.save(friendship);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Tạo bạn bè thành công!")
                .result(null)
                .build());

    }

    @Override
    public Optional<Friend> findByUserId(String userId) {
        return friendRepository.findByAuthorId(userId);
    }

    @Override
    public List<FriendResponse> findFriendUserIdsByUserId(String userId) {
        logger.info("FriendServiceImpl, findFriendUserIdsByUserId");
        Optional<Friend> friendship = friendRepository.findByAuthorId(userId);
        if (friendship.isEmpty()) {
            return new ArrayList<>();
        }
        UserIds userIds = UserIds.builder()
                .userId(friendship.get().getFriendIds())
                .build();
        return userClientService.getFriendByListUserId(userIds);
    }


    @Override
    public ResponseEntity<GenericResponse> findFriendSuggestions(String userIdToken) {
        return null;
    }

    @Override
    public List<FriendResponse> findFriendUserIdsByUserIdPageable(String userId, PageRequest of) {
        logger.info("FriendServiceImpl, findFriendUserIdsByUserIdPageable");
        Optional<Friend> friendship = friendRepository.findByAuthorId(userId);
        if (friendship.isEmpty()) {
            return new ArrayList<>();
        }
        //Lay 5 ban be dau tien
        UserIds userIds = UserIds.builder()
                .userId(friendship.get().getFriendIds().size()> 5
                        ? friendship.get().getFriendIds().subList(0, 5) :
                        friendship.get().getFriendIds())
                .build();
        return userClientService.getFriendByListUserId(userIds);
    }

}
