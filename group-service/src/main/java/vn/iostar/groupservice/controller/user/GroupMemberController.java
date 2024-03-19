package vn.iostar.groupservice.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.request.PostGroupRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.jwt.service.JwtService;
import vn.iostar.groupservice.service.GroupMemberService;

@RestController
@RequestMapping("/api/v1/group-members")
@Slf4j
@RequiredArgsConstructor
public class GroupMemberController {

    private final JwtService jwtService;
    private final GroupMemberService groupMemberService;

    @PostMapping("/acceptMember")
    public ResponseEntity<GenericResponse> acceptMemberPostGroup(
            @RequestHeader("Authorization") String authorizationHeader, @RequestBody PostGroupDTO postGroup) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupMemberService.acceptMemberPostGroup(postGroup, currentUserId);
    }

    /**
     * Lay danh sach thanh vien trong nhom theo Group Id
     *
     * @param postGroupId         postGroupId
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @GetMapping("/list/member/{postGroupId}")
    public ResponseEntity<GenericResponse> getMemberByPostId(@PathVariable("postGroupId") String postGroupId,
                                                             @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupMemberService.getMemberByPostId(postGroupId, currentUserId);
    }

    /**
     * Danh sach member yeu cau vao nhom
     *
     * @param postGroupId         postGroupId
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */

    @GetMapping("/list/memberRequired/{postGroupId}")
    public ResponseEntity<GenericResponse> getMemberRequiredByPostId(@PathVariable("postGroupId") String postGroupId,
                                                                     @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupMemberService.getMemberRequiredByPostId(postGroupId, currentUserId);
    }

    /**
     * Phân quyền phó quản trị viên theo Group Id
     *
     * @param postGroup           postGroup
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @PostMapping("/appoint-deputy")
    public ResponseEntity<GenericResponse> assignDeputyByUserIdAndGroupId(@RequestBody PostGroupRequest postGroup,
                                                                          @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupMemberService.assignDeputyByUserIdAndGroupId(postGroup, currentUserId);
    }

    /**
     * CHuyển quyền admin cho user
     *
     * @param postGroup           postGroup
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @PostMapping("/appoint-admin")
    public ResponseEntity<GenericResponse> assignAdminByUserIdAndGroupId(@RequestBody PostGroupRequest postGroup,
                                                                         @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupMemberService.assignAdminByUserIdAndGroupId(postGroup, currentUserId);
    }

    /**
     * Hủy quyền phó quản trị viên theo Group Id
     *
     * @param postGroup           param postGroup
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @PostMapping("/remove-deputy")
    public ResponseEntity<GenericResponse> removeDeputyByUserIdAndGroupId(@RequestBody PostGroupRequest postGroup,
                                                                          @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupMemberService.removeDeputyByUserIdAndGroupId(postGroup, currentUserId);
    }

    /**
     * Xóa thành viên trong nhóm theo Group Id
     *
     * @param postGroup           postGroup
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @DeleteMapping("/delete/member")
    public ResponseEntity<GenericResponse> deleteMemberByPostId(@RequestBody PostGroupRequest postGroup,
                                                                @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupMemberService.deleteMemberByPostId(postGroup, currentUserId);
    }

    /**
     * Rời khỏi nhóm theo id
     *
     * @param authorizationHeader authorizationHeader
     * @param postGroupId         postGroupId
     * @return GenericResponse
     */
    @PutMapping("/leaveGroup/{postGroupId}")
    public ResponseEntity<GenericResponse> deleteFriend(@RequestHeader("Authorization") String authorizationHeader,
                                                        @Valid @PathVariable("postGroupId") String postGroupId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupMemberService.leaveGroup(currentUserId, postGroupId);
    }

    /**
     * thêm quyền admin cho thành viên trong nhóm
     * Các trường hợp : 1 Nó chưa tham gia nhóm đó và nhóm đó chưa có admin : Thêm
     * thẳng thành viên đó vào nhóm là admin
     * 2 : Nó chưa tham gia và đã có admin: Chuyển quyền admin cho user này
     * 3 Nó đã tham gia và chưa có admin: Thay đổi quyền
     * 4 Nó đã tham gia và đã có admin : Chuyển quyền
     *
     * @param authorizationHeader authorizationHeader
     * @param groupId             groupId
     * @param userId              userId
     * @return GenericResponse
     */
    @PostMapping("/addAdmin")
    public ResponseEntity<GenericResponse> addAdminRoleInGroup(
            @RequestHeader("Authorization") String authorizationHeader, @RequestParam String groupId,
            @RequestParam String userId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupMemberService.addAdminRoleInGroup(groupId, userId, currentUserId);
    }
}
