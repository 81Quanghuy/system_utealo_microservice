package vn.iostar.reportservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.iostar.postservice.dto.response.UserProfileResponse;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/user")
public interface UserClientService {

    @GetMapping("/getUser/{userId}")
    UserProfileResponse getUser(@PathVariable String userId);

}
