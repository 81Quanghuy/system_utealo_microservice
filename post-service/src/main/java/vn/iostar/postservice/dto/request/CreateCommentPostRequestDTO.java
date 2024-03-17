package vn.iostar.postservice.dto.request;


import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateCommentPostRequestDTO {
	private String content;
	private MultipartFile photos;
	private String postId;
}
