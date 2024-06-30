package vn.iostar.scheduleservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iostar.model.RelationshipResponse;
import vn.iostar.scheduleservice.dto.response.UserProfileResponse;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/user")
public interface UserClientService {

    @GetMapping("/getUser/{userId}")
    UserProfileResponse getUser(@PathVariable String userId);

    @GetMapping("/getRelationShip")
    RelationshipResponse getRelationship(@RequestParam String currentId, @RequestParam String userId);
}
