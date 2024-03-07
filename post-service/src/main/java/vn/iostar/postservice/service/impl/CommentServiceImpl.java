package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.repository.CommentRepository;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.service.CommentService;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    @Override
    public Comment createComment() {

        Post post = postRepository.findById("89eb66aa-0154-46fa-925d-e51059fd71fb").orElse(null);
        Comment comment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .content("Hello")
                .userId("1")
                .post(post)
                .commentReply(null)
                .subComments(new ArrayList<>())
                .build();

        commentRepository.save(comment);
        return comment;
    }
}
