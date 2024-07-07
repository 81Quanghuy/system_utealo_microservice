package vn.iostar.postservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iostar.model.UserElastic;
import vn.iostar.postservice.dto.response.UserProfileResponse;

import java.io.IOException;
import java.util.List;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/user")
public interface UserClientService {

    @GetMapping("/getUser/{userId}")
    UserProfileResponse getUser(@PathVariable String userId);
}
