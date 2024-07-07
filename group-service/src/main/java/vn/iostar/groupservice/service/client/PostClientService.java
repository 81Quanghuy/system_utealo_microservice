package vn.iostar.groupservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.dto.response.PostsResponse;
import vn.iostar.model.PostElastic;


import java.io.IOException;
import java.util.List;

@FeignClient(name = "post-service", contextId = "postClientService", path = "/api/v1/post")
public interface PostClientService {
    @GetMapping("/searchPost")
    List<PostsResponse> getPosts(@RequestParam("search") String search);
    @GetMapping("/search")
    List<PostElastic> searchPost(@RequestParam("search") String search,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size)
            throws IOException ;
}
