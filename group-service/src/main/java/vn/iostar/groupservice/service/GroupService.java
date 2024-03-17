package vn.iostar.groupservice.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.groupservice.dto.*;
import vn.iostar.groupservice.dto.request.GroupConfigRequest;
import vn.iostar.groupservice.dto.request.GroupCreateRequest;
import vn.iostar.groupservice.dto.request.UpdateDetailRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.entity.Group;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface GroupService {

    ResponseEntity<GenericResponse> getPostGroupByUserId(String authorizationHeader);

    ResponseEntity<GenericResponse> createGroup(GroupCreateRequest postGroup, String userId);

    ResponseEntity<GenericResponse> updatePostGroupByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId);

    ResponseEntity<GenericResponse> updatePhotoByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId);

    ResponseEntity<GenericResponse> deletePostGroup(String postGroupId, String currentUserId);

    ResponseEntity<GenericResponse> clockGroup(String groupId, String currentUserId);

    ResponseEntity<GenericResponse> getPostGroupById(String currentUserId, String postGroupId);

    ResponseEntity<GenericResponse> getGroupSharePosts(String currentUserId, String postGroupId, Pageable pageable);

    ResponseEntity<GenericResponse> getPostOfPostGroup(String currentUserId, Pageable pageable);

    ResponseEntity<GenericResponse> getGroupPosts(String currentUserId, Integer postGroupId, Pageable pageable);

    List<FilesOfGroupDTO> findLatestFilesByGroupId(Integer groupId);

    Page<PhotosOfGroupDTO> findLatestPhotosByGroupId(Integer groupId, Pageable pageable);
    Optional<Group> findById(String id);
}
