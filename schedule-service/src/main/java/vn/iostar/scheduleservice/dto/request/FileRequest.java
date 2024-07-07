package vn.iostar.scheduleservice.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FileRequest {
    private List<String> userId;
    private String semester;
    private String year;
    private String weekOfSemester;
    private MultipartFile file;
}
