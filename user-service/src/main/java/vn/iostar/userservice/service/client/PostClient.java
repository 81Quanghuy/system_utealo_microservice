package vn.iostar.userservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import vn.iostar.model.DateResponse;

import java.util.Date;

@FeignClient(name = "post-service", contextId = "postClientService", path = "/api/v1")
public interface PostClient {
    @PostMapping("/post/countPosts/{userId}")
    Long countPostsByUserId(@PathVariable String userId, @RequestBody DateResponse dateResponse);
    @PostMapping("/post/comment/countComments/{userId}")
    Long countCommentsByUserId(@PathVariable String userId, @RequestBody DateResponse dateResponse);
    @PostMapping("/share/countShares/{userId}")
    Long countSharesByUserId(@PathVariable String userId, @RequestBody DateResponse dateResponse);

}
