package vn.iotstart.userservice.service;


import java.util.Optional;
import org.springframework.data.domain.Pageable;

import vn.iotstart.userservice.dto.response.UserProfileResponse;
import vn.iotstart.userservice.entity.User;

public interface UserService {
	Optional<User> findById(String id);
	UserProfileResponse getFullProfile(Optional<User> user, Pageable pageable);
}
