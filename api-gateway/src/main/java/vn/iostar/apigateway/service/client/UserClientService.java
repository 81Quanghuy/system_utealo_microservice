package vn.iostar.apigateway.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.iostar.apigateway.config.FeignConfig;
import vn.iostar.apigateway.dto.UserProfileResponse;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/users", configuration = FeignConfig.class)
public interface UserClientService {

    @GetMapping("/getProfileByUserId/{userId}")
    UserProfileResponse getProfileByUserId(@PathVariable String userId);
}
