package vn.iostar.groupservice.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.jwt.service.JwtService;
import vn.iostar.groupservice.service.GroupRequestService;

@RestController
@RequestMapping("/api/v1/groupPost/request")
@Slf4j
@RequiredArgsConstructor
public class GroupRequestController {
    private final JwtService jwtService;
    private final GroupRequestService groupRequestService;

    /**
     * Chap nhan loi moi vao nhom theo Group Id
     * @param authorizationHeader  :authorizationHeader
     * @param postGroupId  :postGroupId
     * @return GenericResponse
     */
    @PostMapping("/accept/{postGroupId}")
    public ResponseEntity<GenericResponse> acceptPostGroup(@RequestHeader("Authorization") String authorizationHeader,
                                                           @PathVariable("postGroupId") String postGroupId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupRequestService.acceptPostGroup(currentUserId, postGroupId);
    }

    // Hủy yêu cầu tham gia nhóm
    @PutMapping("/cancel/request/group/{postGroupId}")
    public ResponseEntity<GenericResponse> cancelRequestPostGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("postGroupId") String postGroupId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupRequestService.cancelRequestPostGroup(postGroupId, currentUserId);
    }

    /**
     *  Từ chối lời mời tham gia nhóm
     * @param authorizationHeader  :authorizationHeader
     * @param postGroupId : postGroupId
     * @return GenericResponse
     */
    @PostMapping("/decline/{postGroupId}")
    public ResponseEntity<GenericResponse> declinePostGroup(@RequestHeader("Authorization") String authorizationHeader,
                                                            @PathVariable("postGroupId") String postGroupId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupRequestService.declinePostGroup(postGroupId, currentUserId);
    }

    /**
     * Mời nhóm hoặc cá nhân tham gia nhóm theo Group Id
     * @param authorizationHeader  :authorizationHeader
     * @param postGroup : postGroup
     * @return GenericResponse
     */
    @PostMapping("/invite")
    public ResponseEntity<GenericResponse> invitePostGroup(@RequestHeader("Authorization") String authorizationHeader,
                                                           @RequestBody PostGroupDTO postGroup) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupRequestService.invitePostGroup(postGroup, currentUserId);
    }

    /**
     *  Gửi yêu cầu vào nhóm
     * @param authorizationHeader :authorizationHeader
     * @param postGroupId : postGroupId
     * @return GenericResponse
     */
    @PostMapping("/joinGroup/{postGroupId}")
    public ResponseEntity<GenericResponse> joinPostGroup(@RequestHeader("Authorization") String authorizationHeader,
                                                         @PathVariable("postGroupId") String postGroupId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupRequestService.joinPostGroup(postGroupId, currentUserId);
    }

    /**
     * Quan tri vien Từ chối yêu cầu tham gia nhóm của ca nhan hoac nhom nguoi
     * @param postGroup postGroup
     * @param authorizationHeader authorizationHeader
     * @return GenericResponse
     */
    @PostMapping("/decline/memberRequired")
    public ResponseEntity<GenericResponse> declineMemberRequiredByPostId(@RequestBody PostGroupDTO postGroup,
                                                                         @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return groupRequestService.declineMemberRequiredByPostId(postGroup, currentUserId);
    }

}
