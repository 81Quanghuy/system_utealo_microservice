package vn.iostar.postservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.entity.Post;

public interface PostService {

    <S extends Post> S save(S entity);
    ResponseEntity<Object> createUserPost(String token, CreatePostRequestDTO requestDTO);

}
