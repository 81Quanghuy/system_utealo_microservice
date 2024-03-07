package vn.iostar.groupservice.service;

import vn.iostar.groupservice.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface GroupRequestService {
    ResponseEntity<GenericResponse> getAllGroupMemberRequests(String userId, String groupId, Optional<String> stateCode);
}
