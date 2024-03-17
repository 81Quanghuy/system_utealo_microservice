package vn.iostar.groupservice.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.groupservice.dto.FilesOfGroupDTO;
import vn.iostar.groupservice.dto.PhotosOfGroupDTO;
import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.request.GroupCreateRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.dto.response.GroupProfileResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.jwt.service.JwtService;
import vn.iostar.groupservice.service.GroupRequestService;
import vn.iostar.groupservice.service.GroupService;
import vn.iostar.groupservice.service.MapperService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/groupPost")
@Slf4j
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final MapperService mapperService;
    private final JwtService jwtService;
    private final GroupRequestService groupRequestService;

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
            @RequestHeader("Authorization") String authorizationHeader) {
        return groupService.getPostGroupByUserId(authorizationHeader);
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
     * Lấy những bài share của group theo id chưa xong
     * @param authorizationHeader authorizationHeader
     * @param postGroupId postGroupId
     * @param page page
     * @param size size
     * @return GenericResponse
     */
    @GetMapping("/{postGroupId}/shares")
    public ResponseEntity<GenericResponse> getGroupSharePosts(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("postGroupId") String postGroupId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return groupService.getGroupSharePosts(currentUserId, postGroupId, pageable);
    }

    /**
     * Lấy tất cả bài viết của group mà user đã tham gia theo id Chưa xong
     * @param authorizationHeader authorizationHeader
     * @param page page
     * @param size size
     * @return GenericResponse
     */

    @GetMapping("/posts")
    public ResponseEntity<GenericResponse> getPostOfUserPostGroup(
            @RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        return groupService.getPostOfPostGroup(currentUserId, pageable);
    }

    /**
     * Lấy tất cả bài viết của group theo id chua lam
     * @param authorizationHeader authorizationHeader
     * @param postGroupId postGroupId
     * @param page page
     * @param size size
     * @return GenericResponse
     */
    @GetMapping("/{postGroupId}/posts")
    public ResponseEntity<GenericResponse> getPostOfPostGroup(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer postGroupId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        return groupService.getGroupPosts(currentUserId, postGroupId,pageable);
    }

    /**
     * Lấy tất cả file trong bài viết của group theo id chua lam
     * @param groupId groupId
     * @return FilesOfGroupDTO
     */

    @GetMapping("/files/{groupId}")
    public List<FilesOfGroupDTO> getLatestFilesOfGroup(@PathVariable("groupId") Integer groupId) {
        return groupService.findLatestFilesByGroupId(groupId);
    }

    /**
     *    Lấy tất cả file trong bài viết của group theo id chua lam
     * @param page page
     * @param size size
     * @param groupId groupId
     * @return PhotosOfGroupDTO
     */
    @GetMapping("/photos/{groupId}")
    public Page<PhotosOfGroupDTO> getLatestPhotoOfGroup(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size, @PathVariable("groupId") Integer groupId) {
        Pageable pageable = PageRequest.of(page, size);
        return groupService.findLatestPhotosByGroupId(groupId, pageable);
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
}
