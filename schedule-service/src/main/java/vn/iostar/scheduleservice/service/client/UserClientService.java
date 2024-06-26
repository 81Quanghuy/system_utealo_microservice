package vn.iostar.scheduleservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.iostar.scheduleservice.dto.response.UserProfileResponse;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/user")
public interface UserClientService {

    @GetMapping("/getUser/{userId}")
    UserProfileResponse getUser(@PathVariable String userId);

}
