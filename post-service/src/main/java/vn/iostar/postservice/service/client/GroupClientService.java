package vn.iostar.postservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.iostar.postservice.dto.response.GroupProfileResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;

@FeignClient(name = "group-service", contextId = "groupClientService", path = "/api/v1/groupPost")
public interface GroupClientService {

    @GetMapping("/getGroup/{groupId}")
    GroupProfileResponse getGroup(@PathVariable String groupId);

}
