package vn.iotstart.groupservice.service;

import org.springframework.http.ResponseEntity;
import vn.iotstart.groupservice.dto.response.GenericResponse;

public interface PostGroupService {

    // Lấy thông tin nhóm
    ResponseEntity<GenericResponse> getPostGroupById(String currentUserId, Integer postGroupId);
}
