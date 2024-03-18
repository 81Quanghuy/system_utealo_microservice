package vn.iostar.postservice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentUpdateRequest {
	

    @NotBlank(message = "Content is required")
    private String content;
    private MultipartFile photos;
}
