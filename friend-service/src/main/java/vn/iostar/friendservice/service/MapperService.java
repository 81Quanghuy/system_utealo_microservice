package vn.iostar.friendservice.service;


import vn.iostar.friendservice.dto.FriendRequestDto;
import vn.iostar.friendservice.dto.FriendshipDto;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.friendservice.entity.FriendRequest;

public interface MapperService {

    FriendRequestDto mapToFriendRequestDto(FriendRequest friendRequest);
    FriendshipDto mapToFriendshipDto(Friend friendship);

}
