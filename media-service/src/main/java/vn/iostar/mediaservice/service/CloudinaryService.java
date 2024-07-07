package vn.iostar.mediaservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadImage(MultipartFile multipartFile, String name, String folder) throws IOException;
    void deleteMediaFile(String url, String folder) throws IOException;
    String uploadMediaFile(MultipartFile mediaFile, String name, String folder) throws IOException;
    void deleteImage(String url, String folder) throws IOException;
    String uploadVideo(MultipartFile imageFile) throws IOException;
    String uploadFile(MultipartFile fileUrl) throws IOException;
    String uploadImage(MultipartFile imageFile) throws IOException;
    void deleteImage(String imageUrl) throws IOException;
    void deleteFile(String fileUrl) throws IOException;
}
