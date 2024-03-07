package vn.iostar.groupservice.controller.user;

import vn.iostar.groupservice.dto.GroupDto;
import vn.iostar.groupservice.dto.SimpleGroupDto;
import vn.iostar.groupservice.dto.request.GroupConfigRequest;
import vn.iostar.groupservice.dto.request.GroupCreateRequest;
import vn.iostar.groupservice.dto.request.UpdateDetailRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.jwt.service.JwtService;
import vn.iostar.groupservice.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/groups")
@Slf4j
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<GenericResponse> createGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @RequestBody @Valid GroupCreateRequest groupCreateRequest) {
        log.info("AdminGroupController, createGroup");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.createGroup(userId, groupCreateRequest);
    }

    @GetMapping
    public ResponseEntity<GenericResponse> getAllGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("AdminGroupController, getAllGroup");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.getGroupsByUserId(userId);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GenericResponse> getGroupById(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId) {
        log.info("AdminGroupController, getGroupById");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.getGroupById(userId, groupId);
    }

    @GetMapping("/validate-user-in-group")
    public ResponseEntity<GenericResponse> validateUserInGroup(@RequestParam("userId") String userId
            , @RequestParam("groupId") String groupId) {
        log.info("AdminGroupController, validateUserInGroup");
        return groupService.valiadateUserInGroup(userId, groupId);
    }

    @GetMapping("/get-group-by-user")
    public ResponseEntity<List<String>> getGroupByUserId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("AdminGroupController, getGroupByUserId");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.getGroupByUserId(userId);
    }

    @PutMapping("/{groupId}/config")
    public ResponseEntity<GenericResponse> updateGroupConfig(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestBody GroupConfigRequest groupConfigRequest) {
        log.info("AdminGroupController, updateGroupConfig");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupConfig(userId, groupId, groupConfigRequest);
    }

    @PutMapping("/{groupId}/updateDetail")
    public ResponseEntity<GenericResponse> updateGroupDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestBody UpdateDetailRequest updateDetailRequest) {
        log.info("AdminGroupController, updateGroupDetail");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupDetail(userId, groupId, updateDetailRequest);
    }

    @PutMapping(value = "/{groupId}/updateAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateGroupAvatar(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestPart("mediaFile") MultipartFile avatar) throws IOException {
        log.info("AdminGroupController, updateGroupAvatar");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupAvatar(userId, groupId, avatar);
    }

    @PutMapping(value = "/{groupId}/updateCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateGroupCover(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId
            , @RequestPart("mediaFile") MultipartFile cover) throws IOException {
        log.info("AdminGroupController, updateGroupCover");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.updateGroupCover(userId, groupId, cover);
    }

    @DeleteMapping(value = "/{groupId}")
    public ResponseEntity<GenericResponse> deleteGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            , @PathVariable("groupId") String groupId) {
        log.info("AdminGroupController, deleteGroup");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.deleteGroup(userId, groupId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GroupDto>> searchGroup(@RequestParam("query") Optional<String> query
            , @RequestParam("isClass") Optional<Boolean> isClass
            , @RequestParam("isPublic") Optional<Boolean> isPublic
            , @RequestParam("grade") Optional<Integer> grade
            , @RequestParam("subject") Optional<String> subject) {
        log.info("AdminGroupController, searchGroup");
        return groupService.searchGroup(query, isClass, isPublic, grade, subject);
    }

    //create group
    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createGroup(
            @RequestBody @Valid GroupCreateRequest groupCreateRequest) {
        log.info("AdminGroupController, createGroup");

        return groupService.createGroupNew(groupCreateRequest);
    }
}
