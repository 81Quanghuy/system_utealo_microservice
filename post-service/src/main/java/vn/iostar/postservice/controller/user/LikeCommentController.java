package vn.iostar.postservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.repository.jpa.LikeRepository;
import vn.iostar.postservice.service.LikeService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment/like")
public class LikeCommentController {

	private final LikeService likeService;
	private final LikeRepository likeRepository;


	// Lấy danh sách like comment
	@GetMapping("/{commentId}")
	public ResponseEntity<GenericResponse> getLikeOfComment(@PathVariable("commentId") String commentId) {
		return likeService.getLikeOfComment(commentId);
	}

	// Lấy số lượng like của comment
	@GetMapping("/number/{commentId}")
	public ResponseEntity<GenericResponse> getNumberLikeOfComment(@PathVariable("commentId") String commentId) {
		return likeService.getCountLikeOfComment(commentId);
	}

	// Like và unlike comment
	@PostMapping("/toggleLike/{commentId}")
	public ResponseEntity<Object> toggleLikeComment(@PathVariable("commentId") String commentId,
			@RequestHeader("Authorization") String token) {
		return likeService.toggleLikeComment(token, commentId);
	}


	// Kiểm tra user đã like comment chưa
	@GetMapping("/checkUser/{commentId}")
	public ResponseEntity<Object> checkUserLikePost(@PathVariable("commentId") String commentId,
			@RequestHeader("Authorization") String token) {
		return likeService.checkUserLikeComment(token, commentId);
	}

	// Lấy danh sách những người đã like comment
	@GetMapping("/listUser/{commentId}")
	public ResponseEntity<Object> listUserLikeComment(@PathVariable("commentId") String commentId) {
		return likeService.listUserLikeComment(commentId);
	}
}
