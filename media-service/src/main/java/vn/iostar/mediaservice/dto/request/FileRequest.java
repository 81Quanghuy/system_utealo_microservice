package vn.iostar.mediaservice.dto.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class FileRequest {
    private String messageId;
    private String groupId;
    private String authorId;
    private List<MultipartFile> file;
}
