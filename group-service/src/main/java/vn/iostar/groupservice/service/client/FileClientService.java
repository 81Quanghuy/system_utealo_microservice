package vn.iostar.groupservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@FeignClient(name = "media-service", contextId = "fileClientService", path = "/api/v1/files")
public interface FileClientService {

    @PostMapping(value = "/uploadPhoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadPhoto(@RequestPart("mediaFile") MultipartFile file);

    @DeleteMapping(value = "/deletePhoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void deletePhoto(@RequestPart("refUrl") String refUrl) throws IOException;
}
