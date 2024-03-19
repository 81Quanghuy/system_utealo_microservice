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

    @PostMapping(value = "/uploadGroupAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadGroupAvatar(@RequestPart("mediaFile") MultipartFile file) throws IOException;

    @DeleteMapping(value = "/deleteGroupAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void deleteGroupAvatar(@RequestPart("refUrl") String refUrl) throws IOException;

    @PostMapping(value = "/uploadGroupCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadGroupCover(@RequestPart("mediaFile") MultipartFile file) throws IOException;

    @DeleteMapping(value = "/deleteGroupCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void deleteGroupCover(@RequestPart("refUrl") String refUrl) throws IOException;

}
