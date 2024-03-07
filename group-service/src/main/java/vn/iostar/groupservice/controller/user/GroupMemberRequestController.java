package vn.iostar.groupservice.controller.user;

import vn.iostar.groupservice.dto.request.StateRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.jwt.service.JwtService;
import vn.iostar.groupservice.service.GroupRequestService;
import vn.iostar.groupservice.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/group-member-requests")
@Slf4j
@RequiredArgsConstructor
public class GroupMemberRequestController {
    private final JwtService jwtService;
    private final GroupRequestService groupMemberRequestService;
    private final GroupMemberService groupMemberService;

    @GetMapping
    public ResponseEntity<GenericResponse> getAllGroupMemberRequests(@RequestHeader("Authorization") String authorizationHeader,
                                                                     @RequestParam(value = "groupId") String groupId,
                                                                     @RequestParam("state") Optional<String> stateCode) {
        log.info("GroupMemberRequestController, ResponseEntity<GenericResponse> getAllGroupMemberRequests");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberRequestService.getAllGroupMemberRequests(userId, groupId, stateCode);
    }

    @PostMapping("/{gmrId}/response")
    public ResponseEntity<GenericResponse> responseGroupMemberRequest(@RequestHeader("Authorization") String authorizationHeader,
                                                                      @PathVariable("gmrId") String groupMemberRequestId,
                                                                      @RequestBody StateRequest stateRequest) {
        log.info("AdminGroupMemberController, ResponseEntity<GenericResponse> responseGroupMemberRequest");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return groupMemberService.responseGroupMemberRequest(userId, groupMemberRequestId, stateRequest);
    }
}
