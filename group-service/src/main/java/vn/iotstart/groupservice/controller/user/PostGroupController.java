package vn.iotstart.groupservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iotstart.groupservice.dto.response.GenericResponse;
import vn.iotstart.groupservice.service.PostGroupService;
import vn.iotstart.groupservice.service.client.UserClientService;

@RestController
@RequestMapping("/api/v1/groupPost")
@RequiredArgsConstructor
public class PostGroupController {

    @Autowired
    PostGroupService groupService;


    private final  UserClientService userClientService;

    // Lấy thông tin nhóm
    @GetMapping("/get/{postGroupId}")
    public ResponseEntity<GenericResponse> getPostGroupById(@RequestHeader("Authorization") String authorizationHeader,
                                                            @PathVariable("postGroupId") Integer postGroupId) {
        String currentUserId = userClientService.getUserId(authorizationHeader);
        System.out.println("currentUserId: " + currentUserId);

        return groupService.getPostGroupById(currentUserId, postGroupId);
    }

}
