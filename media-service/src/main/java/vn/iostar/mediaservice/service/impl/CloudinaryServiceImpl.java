package vn.iostar.mediaservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.mediaservice.exception.wrapper.UnsupportedMediaTypeException;
import vn.iostar.mediaservice.service.CloudinaryService;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private static final String ROOT_FOLDER = "stem-microservice-backend";

    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile multipartFile, String name, String folder) throws IOException {
        log.info("CloudinaryServiceImpl, uploadImage");
        if (multipartFile.isEmpty()) {
            throw new UnsupportedMediaTypeException("File is null. Please upload a valid file.");
        }
        if (!Objects.requireNonNull(multipartFile.getContentType()).startsWith("image/")) {
            throw new UnsupportedMediaTypeException("Only image files are allowed.");
        }
        var params = ObjectUtils.asMap(
                "folder", ROOT_FOLDER + folder,
                "public_id", name,
                "resource_type", "image");
        var uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), params);
        log.info("CloudinaryServiceImpl, uploadImage, uploadResult: {}", uploadResult.get("secure_url").toString());
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public String uploadMediaFile(MultipartFile mediaFile, String name, String folder) throws IOException {
        log.info("CloudinaryServiceImpl, uploadMediaFiles");
        if (mediaFile.isEmpty()) {
            throw new UnsupportedMediaTypeException("File is null. Please upload a valid file.");
        }
        var params = ObjectUtils.asMap(
                "folder", ROOT_FOLDER + folder,
                "public_id", name,
                "resource_type", "auto");
        var uploadResult = cloudinary.uploader().upload(mediaFile.getBytes(), params);
        log.info("CloudinaryServiceImpl, uploadMediaFiles, uploadResult: {}", uploadResult.get("secure_url").toString());
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public void deleteImage(String url, String folder) throws IOException {
        var params = ObjectUtils.asMap(
                "folder", ROOT_FOLDER + folder,
                "resource_type", "image");
        var result = cloudinary.uploader().destroy(getPublicIdFromUrl(url, folder), params);
        log.info("CloudinaryServiceImpl, deleteImage, result: {}", result.get("result").toString());
    }

    @Override
    public String deleteMediaFile(String url, String folder) throws IOException {
        var params = ObjectUtils.asMap(
                "folder", ROOT_FOLDER + folder,
                "resource_type", "image");
        var result = cloudinary.uploader().destroy(getPublicIdFromUrl(url, folder), params);
        log.info("CloudinaryServiceImpl, deleteUserImage, result: {}", result.get("result").toString());
        return result.get("result").toString();
    }

    public String getPublicIdFromUrl(String imageUrl, String folder) {
        String publicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
        return ROOT_FOLDER + folder + "/" + publicId;
    }

    @Override
    public String uploadVideo(MultipartFile imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("File is null. Please upload a valid file.");
        }
        if (!Objects.requireNonNull(imageFile.getContentType()).startsWith("video/")) {
            throw new IllegalArgumentException("Only video files are allowed.");
        }

        Map params = ObjectUtils.asMap("folder", "Social Media/User", "resource_type", "video");
        Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public String uploadImage(MultipartFile imageFile) throws IOException {

        if (imageFile == null) {
            throw new IllegalArgumentException("File is null. Please upload a valid file.");
        }
        if (!Objects.requireNonNull(imageFile.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        Map params = ObjectUtils.asMap("folder", "Social Media/User", "resource_type", "image");
        Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File is null. Please upload a valid file.");
        }

        String originalFileName = file.getOriginalFilename();
        String publicId = "Social Media/User/" + originalFileName;

        Map params = ObjectUtils.asMap("public_id", publicId, "resource_type", "auto");
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public void deleteImage(String imageUrl) throws IOException {
        Map<String, String> params = ObjectUtils.asMap("folder", "Social Media/User", "resource_type", "image");
        Map result = cloudinary.uploader().destroy(getPublicIdImage(imageUrl), params);
        System.out.println(result.get("result").toString());
    }

    public String getPublicIdImage(String imageUrl)  {
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
        String publicId = "Social Media/User/" + imageName;
        return publicId;
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        Map<String, String> params = ObjectUtils.asMap("folder", "Social Media/User", "resource_type", "auto");
        Map result = cloudinary.uploader().destroy(getPublicIdFile(fileUrl), params);
        System.out.println(result.get("result").toString());
    }

    public String getPublicIdFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        String publicId = "Social Media/User/" + fileName;
        return publicId;
    }
}
