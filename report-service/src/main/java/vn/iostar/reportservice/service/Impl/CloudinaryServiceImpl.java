package vn.iostar.reportservice.service.Impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.reportservice.service.CloudinaryService;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

	@Autowired
	Cloudinary cloudinary;

	@Override
	public String uploadImage(MultipartFile imageFile) throws IOException {
		
		if (imageFile == null) {
			throw new IllegalArgumentException("File is null. Please upload a valid file.");
		}
		if (!imageFile.getContentType().startsWith("image/")) {
			throw new IllegalArgumentException("Only image files are allowed.");
		}
		
		Map<String, String> params = ObjectUtils.asMap("folder", "Social Media/User", "resource_type", "image");
		Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), params);
		return (String) uploadResult.get("secure_url");
	}

	@Override
	public String uploadVideo(MultipartFile imageFile) throws IOException {
		if (imageFile == null) {
			throw new IllegalArgumentException("File is null. Please upload a valid file.");
		}
		if (!imageFile.getContentType().startsWith("video/")) {
			throw new IllegalArgumentException("Only video files are allowed.");
		}

		Map<String, String> params = ObjectUtils.asMap("folder", "Social Media/User", "resource_type", "video");
		Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), params);
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
	public String uploadFile(MultipartFile file) throws IOException {
	    if (file == null) {
	        throw new IllegalArgumentException("File is null. Please upload a valid file.");
	    }
	    
	    String originalFileName = file.getOriginalFilename();
	    String fileExtension = StringUtils.getFilenameExtension(originalFileName);
	    String publicId = "Social Media/User/" + originalFileName; // Sử dụng tên gốc làm public_id

	    Map<String, String> params = ObjectUtils.asMap("public_id", publicId, "resource_type", "auto");
	    Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
	    return (String) uploadResult.get("secure_url");
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
