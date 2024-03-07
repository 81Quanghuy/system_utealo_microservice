package vn.iostar.friendservice.config.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserServiceClient {
    @GetMapping("/user")
    String test();
}
