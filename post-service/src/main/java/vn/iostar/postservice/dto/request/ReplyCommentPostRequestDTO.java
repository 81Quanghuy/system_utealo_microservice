package vn.iostar.postservice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ReplyCommentPostRequestDTO {
	private String content;
	private MultipartFile photos;
	private String postId;
	private String commentId;
}
