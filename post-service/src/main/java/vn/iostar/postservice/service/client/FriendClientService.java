package vn.iostar.postservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "friend-service", contextId = "friendClientService", path = "/api/v1/friend")
public interface FriendClientService {
    @GetMapping("/list/friend-ids/{userId}")
    List<String> getFriendIdsByUserId(@PathVariable String userId);

}
