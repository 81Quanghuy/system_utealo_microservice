package vn.iostar.friendservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.iostar.friendservice.dto.UserIds;
import vn.iostar.friendservice.dto.response.FriendResponse;

import java.util.List;
@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/user")
public interface UserClientService {

    @PostMapping("/getProfileByListUserId")
    List<FriendResponse> getFriendByListUserId(@RequestBody UserIds list_userId) ;
}
