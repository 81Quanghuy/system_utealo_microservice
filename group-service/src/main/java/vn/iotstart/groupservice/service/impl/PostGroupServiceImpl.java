package vn.iotstart.groupservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iotstart.groupservice.dto.response.GenericResponse;
import vn.iotstart.groupservice.dto.response.PostGroupResponse;
import vn.iotstart.groupservice.dto.response.UserProfileResponse;
import vn.iotstart.groupservice.entity.PostGroup;
import vn.iotstart.groupservice.repository.PostGroupRepository;
import vn.iotstart.groupservice.service.PostGroupService;
import vn.iotstart.groupservice.service.client.UserClientService;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostGroupServiceImpl implements PostGroupService {


    private final UserClientService userClientService;

    @Autowired
    PostGroupRepository postGroupRepository;

    @Override
    public ResponseEntity<GenericResponse> getPostGroupById(String currentUserId, Integer postGroupId) {
//        ResponseEntity<GenericResponse<UserProfileResponse>> user = userClientService.getUser(currentUserId);
        UserProfileResponse user = userClientService.getUser(currentUserId);
        if (user.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
        Optional<PostGroup> postGroup = postGroupRepository.findById(postGroupId);
        if (postGroup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }

        PostGroupResponse response = new PostGroupResponse(postGroup.get());
        //response.setRoleGroup(checkUserInGroup(user.get(), postGroup.get()));
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get successfully").result(response)
                .statusCode(HttpStatus.OK.value()).build());
    }

    // Kiểm tra user có trong nhóm không
//    public String checkUserInGroup(User user, PostGroup group) {
//        Set<PostGroupMember> member = group.getPostGroupMembers();
//        for (PostGroupMember postGroupMember : member) {
//            if (postGroupMember.getUser().equals(user)) {
//                if (postGroupMember.getRoleUserGroup().equals(RoleUserGroup.Admin)) {
//                    return "Admin";
//                } else if (postGroupMember.getRoleUserGroup().equals(RoleUserGroup.Deputy)) {
//                    return "Deputy";
//                }
//                return "Member";
//            }
//        }
//        Optional<PostGroupRequest> postGroupRequest = postGroupRequestRepository
//                .findByInvitedUserUserIdAndPostGroupPostGroupId(user.getUserId(), group.getPostGroupId());
//        if (postGroupRequest.isPresent()) {
//            if (Boolean.TRUE.equals(postGroupRequest.get().getIsAccept())) {
//                return "Waiting Accept";
//            }
//            return "Accept Invited";
//        }
//        return "None";
//    }
}
