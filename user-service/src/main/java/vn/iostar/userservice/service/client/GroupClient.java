package vn.iostar.userservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.constant.AdminInGroup;
import vn.iostar.model.GroupResponse;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.entity.User;

import java.util.List;

@FeignClient(name = "group-service", contextId = "groupClientService", path = "/api/v1/groupPost")
public interface GroupClient {

    @PostMapping("/update/group-from-excel")
    public ResponseEntity<GenericResponse> updateGroupFromExcel(@RequestBody List<GroupResponse> groupResponse);

    // check admin in group
    @GetMapping("/check-admin-in-group")
    public AdminInGroup checkAdminInGroup(@RequestParam("name") String groupName);

    @DeleteMapping("/delete-member-in-group")
    void deleteMemberInGroup(@RequestBody List<String> userIds);

    @PostMapping("/add-member-to-group")
    public ResponseEntity<GenericResponse> addMemberToGroup(@RequestBody GroupResponse groupResponse);
}
