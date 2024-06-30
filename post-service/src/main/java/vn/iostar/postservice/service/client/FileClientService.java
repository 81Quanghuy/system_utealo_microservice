package vn.iostar.postservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@FeignClient(name = "media-service", contextId = "fileClientService", path = "/api/v1/files")
public interface FileClientService {

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestPart("mediaFile") MultipartFile file) throws IOException;

    @PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadImage(@RequestPart("mediaFile") MultipartFile file) throws IOException;

    @PostMapping(value = "/uploadVideo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadVideo(@RequestPart("mediaFile") MultipartFile file) throws IOException;

}
