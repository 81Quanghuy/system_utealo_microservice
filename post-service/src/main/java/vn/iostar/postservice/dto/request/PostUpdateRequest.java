package vn.iostar.postservice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import vn.iostar.postservice.constant.PrivacyLevel;

@Data
public class PostUpdateRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Location is required")
    private String location;

    private String photoUrl;
    private String fileUrl;
    private MultipartFile photos;
    private MultipartFile files;
    private PrivacyLevel privacyLevel;
    
}
