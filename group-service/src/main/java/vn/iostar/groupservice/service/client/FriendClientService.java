package vn.iostar.groupservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iostar.groupservice.dto.response.GenericResponse;

import java.util.List;

@FeignClient(name = "friend-service", contextId = "friendClientService", path = "/api/v1/friend")
public interface FriendClientService {

    @GetMapping("/status")
    ResponseEntity<GenericResponse> getStatusByUserId(@RequestParam("userId") String userId, @RequestParam("userIdToken") String userIdToken);

}
