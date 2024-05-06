package vn.iostar.groupservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iostar.groupservice.dto.response.PostsResponse;


import java.util.List;

@FeignClient(name = "post-service", contextId = "postClientService", path = "/api/v1/post")
public interface PostClientService {
    @GetMapping("/searchPost")
    List<PostsResponse> getPosts(@RequestParam("search") String search);
}
