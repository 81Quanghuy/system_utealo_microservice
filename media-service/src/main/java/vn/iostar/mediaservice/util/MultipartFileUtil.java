package vn.iostar.mediaservice.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MultipartFileUtil {
    public static Boolean isValidMultipartFiles(List<MultipartFile> multipartFileList) {
        if (multipartFileList == null) {
            return false;
        }
        for (MultipartFile multipartFile : multipartFileList) {
            if (multipartFile.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
