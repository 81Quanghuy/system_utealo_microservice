package vn.iostar.groupservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import vn.iostar.groupservice.dto.SearchUser;
import vn.iostar.groupservice.dto.UserIds;
import vn.iostar.groupservice.dto.response.FriendResponse;
import vn.iostar.groupservice.dto.response.UserProfileResponse;

import java.util.List;

@FeignClient(name = "user-service", contextId = "userClientService", path = "/api/v1/user")
public interface UserClientService {
    @GetMapping("/getProfileByUserId/{userId}")
    UserProfileResponse getProfileByUserId(@PathVariable String userId);
    @PostMapping("/getProfileByListUserId")
    List<FriendResponse> getFriendByListUserId(@RequestBody UserIds list_userId) ;
    @GetMapping("/getUser/{userId}")
    UserProfileResponse getUser(@PathVariable String userId);
    @GetMapping("/searchUser")
    List<SearchUser> getUsersByName(@RequestParam("search") String search);
}
