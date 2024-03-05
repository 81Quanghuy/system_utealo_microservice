package vn.iotstart.groupservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import vn.iotstart.groupservice.dto.response.UserProfileResponse;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/user")
public interface UserClientService {
    @GetMapping("/getUser/{userId}")
//    public ResponseEntity<GenericResponse> getUser(@PathVariable("userId") String userId);
    UserProfileResponse getUser(@PathVariable String userId);

    @GetMapping("/getUserId")
    public String getUserId(@RequestHeader("Authorization") String authorizationHeader);
}
