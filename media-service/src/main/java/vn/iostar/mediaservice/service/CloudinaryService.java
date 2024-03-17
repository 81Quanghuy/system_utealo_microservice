package vn.iostar.mediaservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CloudinaryService {
    String uploadImage(MultipartFile multipartFile, String name, String folder) throws IOException;
    String deleteMediaFile(String url, String folder) throws IOException;

    String uploadMediaFile(MultipartFile mediaFile, String name, String folder) throws IOException;

    void deleteImage(String url, String folder) throws IOException;
}
