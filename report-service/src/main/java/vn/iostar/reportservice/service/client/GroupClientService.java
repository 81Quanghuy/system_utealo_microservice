package vn.iostar.reportservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.iostar.postservice.dto.response.GroupProfileResponse;

import java.util.List;

@FeignClient(name = "group-service", contextId = "groupClientService", path = "/api/v1/groupPost")
public interface GroupClientService {

    @GetMapping("/getGroup/{groupId}")
    GroupProfileResponse getGroup(@PathVariable String groupId);

    @GetMapping("/list/group-ids/{userId}")
    List<String> getGroupIdsByUserId(@PathVariable String userId);

    @GetMapping("/list/admins/{groupId}")
    List<String> getAdminsInGroup(@PathVariable String groupId) ;

}
