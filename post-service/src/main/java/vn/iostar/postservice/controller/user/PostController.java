package vn.iostar.postservice.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.service.PostService;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    @Autowired
    PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Object> createUserPost(@ModelAttribute CreatePostRequestDTO requestDTO,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return postService.createUserPost(token, requestDTO);
    }

}
