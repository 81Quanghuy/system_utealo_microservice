package vn.iostar.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.iostar.userservice.repository.UserRepository;
import vn.iostar.userservice.dto.response.UserProfileResponse;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.repository.UserRepository;
import vn.iostar.userservice.service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;


	// Lấy toàn bộ thông tin người dùng
	@Override
	public UserProfileResponse getFullProfile(Optional<User> user, Pageable pageable) {
		UserProfileResponse profileResponse = new UserProfileResponse(user.get());
//		List<FriendResponse> fResponse = friendRepository.findFriendUserIdsByUserId(user.get().getUserId());
//		profileResponse.setFriends(fResponse);
//
//		List<GroupPostResponse> groupPostResponses = postGroupRepository
//				.findPostGroupInfoByUserId(user.get().getUserId(), pageable);
//		profileResponse.setPostGroup(groupPostResponses);
		return profileResponse;
	}

	@Override
	public Optional<User> findById(String id) {
		return userRepository.findById(id);
	}

}