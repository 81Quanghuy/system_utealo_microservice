package vn.iostar.friendservice.service.impl;

import org.springframework.stereotype.Service;
import vn.iostar.friendservice.constant.AppConstant;
import vn.iostar.friendservice.dto.FriendRequestDto;
import vn.iostar.friendservice.dto.FriendshipDto;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.friendservice.entity.FriendRequest;
import vn.iostar.friendservice.service.MapperService;
import vn.iostar.friendservice.util.DateUtil;

@Service
public class MapperServiceImpl implements MapperService {
    @Override
    public FriendRequestDto mapToFriendRequestDto(FriendRequest friendRequest) {
        return FriendRequestDto.builder()
                .id(friendRequest.getId())
                .senderId(friendRequest.getSenderId())
                .recipientId(friendRequest.getRecipientId())
                .status(friendRequest.getState().toString())
                .createdAt(friendRequest.getCreatedAt() == null ?
                        null : DateUtil.date2String(friendRequest.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(friendRequest.getUpdatedAt() == null ?
                        null : DateUtil.date2String(friendRequest.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public FriendshipDto mapToFriendshipDto(Friend friendship) {
        return FriendshipDto.builder()
                .id(friendship.getId())
                .authorId(friendship.getAuthorId())
                .friendIds(friendship.getFriendIds())
                .createdAt(friendship.getCreatedAt() == null ?
                        null : DateUtil.date2String(friendship.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(friendship.getUpdatedAt() == null ?
                        null : DateUtil.date2String(friendship.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }
}
