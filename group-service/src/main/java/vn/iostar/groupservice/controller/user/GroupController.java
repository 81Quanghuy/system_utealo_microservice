package vn.iostar.groupservice.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.constant.AdminInGroup;
import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.request.GroupCreateRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.dto.response.GroupProfileResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.entity.GroupMember;
import vn.iostar.groupservice.jwt.service.JwtService;
import vn.iostar.groupservice.model.GroupDocument;
import vn.iostar.groupservice.repository.jpa.GroupMemberRepository;
import vn.iostar.groupservice.service.GroupRequestService;
import vn.iostar.groupservice.service.GroupService;
import vn.iostar.groupservice.service.MapperService;
import vn.iostar.groupservice.service.synchronization.GroupSynchronizationService;
import vn.iostar.model.GroupResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/groupPost")
@Slf4j
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final MapperService mapperService;
    private final JwtService jwtService;
    private final GroupRequestService groupRequestService;
    private final GroupMemberRepository groupMemberRepository;

    /**
     *  Create group by authorized user and GroupCreateRequest
     * @param postGroup postGroup
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createGroupByUser(@RequestBody  @Valid  GroupCreateRequest postGroup,
                                                             @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupService.createGroup(postGroup, userId);
    }

    /**
     * Get group by user id
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @GetMapping("/list/all")
    public ResponseEntity<GenericResponse> getPostGroupByUserId(
            @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
        return groupService.getPostGroupByUserId(authorizationHeader);
    }

    /**
     * Get group join by user id
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @GetMapping("/list/join")
    public ResponseEntity<GenericResponse> getPostGroupJoinByUserId(
            @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupService.getPostGroupJoinByUserId(currentUserId);
    }

    /**
     * Get group owner by user id
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @GetMapping("/list/owner")
    public ResponseEntity<GenericResponse> getPostGroupOwnerByUserId(
            @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupService.getPostGroupOwnerByUserId(currentUserId);
    }


    /**
     * Lấy danh sách các group mà user đã được mời tham gia
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @GetMapping("/list/isInvited")
    public ResponseEntity<GenericResponse> getPostGroupInvitedByUserId(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupRequestService.getPostGroupInvitedByUserId(currentUserId);
    }

    /**
     *  Laay danh sach loi moi nhom da gui di theo user id
     *
     * @param authorizationHeader authorizationHeader
     * @return  GenericResponse
     */
    @GetMapping("/list/invited")
    public ResponseEntity<GenericResponse> getPostGroupRequestsSentByUserId(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupRequestService.getPostGroupRequestsSentByUserId(currentUserId);
    }

    /**
     *  Cập nhật description của group theo id
     * @param authorizationHeader authorizationHeader
     * @param postGroup postGroup
     * @return GenericResponse
     */
    @PutMapping("/update/bio")
    public ResponseEntity<GenericResponse> updatePostGroupByPostId(
            @RequestHeader("Authorization") String authorizationHeader, @RequestBody PostGroupDTO postGroup) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupService.updatePostGroupByPostIdAndUserId(postGroup, currentUserId);
    }

    /**
     *  Update avatar and background of group by id
     * @param authorizationHeader authorizationHeader
     * @param postGroup postGroup
     * @return GenericResponse
     */
    @PutMapping("/update/photo")
    public ResponseEntity<GenericResponse> updateBackgroundByPostId(
            @RequestHeader("Authorization") String authorizationHeader, @ModelAttribute PostGroupDTO postGroup) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupService.updatePhotoByPostIdAndUserId(postGroup, currentUserId);
    }

    /**
     * Delete group by id
     * @param authorizationHeader  authorizationHeader
     * @param postGroupId postGroupId
     * @return GenericResponse
     */
    @DeleteMapping("/delete/{postGroupId}")
    public ResponseEntity<GenericResponse> deletePostGroup(@RequestHeader("Authorization") String authorizationHeader,
                                                           @PathVariable("postGroupId") String postGroupId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupService.deletePostGroup(postGroupId, currentUserId);
    }

    /**
     * Chuyen doi trang thai group by id
     * @param authorizationHeader authorizationHeader
     * @param groupId groupId
     * @return GenericResponse
     */
    @PutMapping("/toggleState/{groupId}")
    public ResponseEntity<GenericResponse> clockGroup(@RequestHeader("Authorization") String authorizationHeader,
                                                      @PathVariable("groupId") String groupId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupService.clockGroup(groupId, currentUserId);
    }

    /**
     * Get inf group by ID
     * @param authorizationHeader authorizationHeader
     * @param postGroupId postGroupId
     * @return GenericResponse
     */
    @GetMapping("/get/{postGroupId}")
    public ResponseEntity<GenericResponse> getPostGroupById(@RequestHeader("Authorization") String authorizationHeader,
                                                            @PathVariable("postGroupId") String postGroupId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupService.getPostGroupById(currentUserId, postGroupId);
    }

    /**
     * Tìm kiếm nhóm theo tên
     * @param authorizationHeader authorizationHeader
     * @param search search
     * @return GenericResponse
     */
    @GetMapping("/getPostGroups/key")
    public ResponseEntity<GenericResponse> searchPostGroups(@RequestHeader("Authorization") String authorizationHeader,
                                                            @RequestParam("search") String search) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupService.findByPostGroupNameContainingIgnoreCase(search, currentUserId);
    }

    // Group Client
    @GetMapping("/getGroup/{groupId}")
    public GroupProfileResponse getGroup(@PathVariable String groupId) {
        Optional<Group> group = groupService.findById(groupId);

        if (group.isEmpty()) {
            throw new RuntimeException("Group not found.");
        }
        return mapperService.mapToGroupProfileResponse(group.get());

    }

    // Lấy danh sách id của nhóm theo userId
    @GetMapping("/list/group-ids/{userId}")
    public List<String> getGroupIdsByUserId(@PathVariable String userId) {
        List<GroupMember> groupMembers = groupMemberRepository.findByUserId(userId);
        return groupMembers.stream()
                .map(groupMember -> groupMember.getGroup().getId())
                .collect(Collectors.toList());
    }

    // Lấy danh sách userId là admin trong 1 nhóm
    @GetMapping("/list/admins/{groupId}")
    public List<String> getAdminsInGroup(@PathVariable String groupId) {
        List<GroupMember> groupMembers = groupMemberRepository.findUserIdAdminInGroup(groupId);
        return groupMembers.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toList());
    }

    // Tìm kiếm bài viết, user, nhóm
    @GetMapping("/search/key")
    public ResponseEntity<GenericResponse> searchAll(@RequestHeader("Authorization") String authorizationHeader,
                                                            @RequestParam("search") String search) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return groupService.searchGroupAndUserContainingIgnoreCase(search, userIdToken);
    }

    // Tìm kiếm các nhóm là nhóm của hệ thống
    @GetMapping("/list/system")
    public ResponseEntity<GenericResponse> getSystemGroups() {
        return groupService.getSystemGroups();
    }

    // tạo thông tin nhóm từ file excel
    @PostMapping("/update/group-from-excel")
    public ResponseEntity<GenericResponse> updateGroupFromExcel(@RequestBody List<GroupResponse> groupResponse) {
        return groupService.updateGroupFromExcel(groupResponse);
    }
    // check admin in group
    @GetMapping("/check-admin-in-group")
    public AdminInGroup checkAdminInGroup(@RequestParam("name")  String groupName){
        return groupService.checkAdminInGroup(groupName);
    }

    // add member to group system
    @PostMapping("/add-member-to-group")
    public ResponseEntity<GenericResponse> addMemberToGroup(@RequestBody GroupResponse groupResponse){
        return groupService.addMemberToSystemGroup(groupResponse);
    }

    @DeleteMapping("/delete-member-in-group")
    void deleteMemberInGroup(@RequestBody List<String> userIds){
        groupService.deleteMemberInGroup(userIds);
    }

    //search group by name
    @GetMapping("/search")
    public ResponseEntity<GenericResponse> searchGroupByName(@RequestParam("key") String key,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size)
            throws IOException {
        return groupService.searchKey(key.trim(),page,size);
    }
}
