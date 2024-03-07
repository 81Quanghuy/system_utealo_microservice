package vn.iostar.postservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.service.PostService;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    @Autowired
    PostService postService;
    @PostMapping("/create")
    public ResponseEntity<Post> createPost() {
        return ResponseEntity.ok(postService.createPost());
    }

}
