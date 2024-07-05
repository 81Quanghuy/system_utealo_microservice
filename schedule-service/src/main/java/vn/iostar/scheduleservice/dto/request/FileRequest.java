package vn.iostar.scheduleservice.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileRequest {
    private MultipartFile file;
}
