package vn.iostar.postservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CreateCommentShareRequestDTO;
import vn.iostar.postservice.dto.request.ReplyCommentShareRequestDTO;
import vn.iostar.postservice.repository.jpa.CommentRepository;
import vn.iostar.postservice.service.CommentService;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/share/comment")
public class CommentShareController {



	private final CommentRepository commentRepository;
	private final CommentService commentService;

	@GetMapping("/{shareId}")
	public ResponseEntity<GenericResponse> getCommentOfShare(@PathVariable("shareId") String shareId) {
		return commentService.getCommentOfShare(shareId);
	}
	
	@GetMapping("/{commentId}/commentReply")
	public ResponseEntity<GenericResponse> getCommentReplyOfComment(@PathVariable("commentId") String commentId) {
		return commentService.getCommentReplyOfCommentShare(commentId);
	}
	
	@PostMapping("/create")
	public ResponseEntity<Object> createCommentShare(@ModelAttribute CreateCommentShareRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return commentService.createCommentShare(token, requestDTO);
	}
	
	@PostMapping("/reply")
	public ResponseEntity<Object> replyCommentPost(@ModelAttribute ReplyCommentShareRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return commentService.replyCommentShare(token, requestDTO);
	}
}
