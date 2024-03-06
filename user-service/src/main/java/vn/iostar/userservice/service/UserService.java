package vn.iostar.userservice.service;

import org.springframework.data.domain.Pageable;
import vn.iostar.userservice.dto.response.UserProfileResponse;
import vn.iostar.userservice.entity.User;

import java.util.Optional;

public interface UserService {
	Optional<User> findById(String id);
	UserProfileResponse getFullProfile(Optional<User> user, Pageable pageable);
}
