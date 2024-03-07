package vn.iostar.groupservice.service;


import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.groupservice.dto.GroupDto;
import vn.iostar.groupservice.dto.SimpleGroupDto;
import vn.iostar.groupservice.dto.request.GroupConfigRequest;
import vn.iostar.groupservice.dto.request.GroupCreateRequest;
import vn.iostar.groupservice.dto.request.UpdateDetailRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface GroupService {
    ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest);

    ResponseEntity<GenericResponse> getGroupById(String userId, String groupId);

    ResponseEntity<GenericResponse> getGroupsByUserId(String userId);

    ResponseEntity<GenericResponse> updateGroupConfig(String userId, String groupId, GroupConfigRequest groupConfigRequest);

    ResponseEntity<GenericResponse> updateGroupDetail(String userId, String groupId, UpdateDetailRequest updateDetailRequest);

    ResponseEntity<GenericResponse> updateGroupAvatar(String userId, String groupId, MultipartFile avatar) throws IOException;

    ResponseEntity<GenericResponse> updateGroupCover(String userId, String groupId, MultipartFile cover) throws IOException;

    ResponseEntity<GenericResponse> deleteGroup(String userId, String groupId);

    ResponseEntity<List<GroupDto>> searchGroup(Optional<String> query, Optional<Boolean> isClass, Optional<Boolean> isPublic, Optional<Integer> grade, Optional<String> subject);

    ResponseEntity<GenericResponse> valiadateUserInGroup(String userId, String groupId);

    ResponseEntity<List<String>> getGroupByUserId(String userId);


    ResponseEntity<GenericResponse> getAllGroupsForAdmin(String token, Integer page, Integer size);


    ResponseEntity<GenericResponse> getAllClassesForAdmin(String token, Integer page, Integer size);

    ResponseEntity<GenericResponse> createGroupNew(GroupCreateRequest groupCreateRequest);
}
