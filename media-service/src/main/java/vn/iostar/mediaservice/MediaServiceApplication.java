package vn.iostar.mediaservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import vn.iostar.mediaservice.constant.FileEnum;
import vn.iostar.mediaservice.entity.FileType;
import vn.iostar.mediaservice.repository.FileTypeRepository;

import java.util.Date;
import java.util.List;

@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "Media API", version = "1.0", description = "Documentation Media API v1.0")
)
public class MediaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MediaServiceApplication.class, args);
    }

    @Autowired
    private FileTypeRepository fileTypeRepository;

    @Bean
    InitializingBean sendDatabase() {
        return () -> {
            Date now = new Date();
            if (fileTypeRepository.findByName(FileEnum.IMAGE.getId()).isEmpty()) {
                fileTypeRepository.save(
                        FileType.builder()
                                .id(FileEnum.IMAGE.getId())
                                .name(FileEnum.IMAGE.getName())
                                .extension(List.of("jpg", "png", "jpeg", "gif","bmp","svg","webp","ico","tiff","psd","raw"))
                                .createdAt(now)
                                .build()
                );
            }
            if (fileTypeRepository.findByName(FileEnum.VIDEO.getId()).isEmpty()) {
                fileTypeRepository.save(
                        FileType.builder()
                                .id(FileEnum.VIDEO.getId())
                                .name(FileEnum.VIDEO.getName())
                                .extension(List.of("mp4", "avi", "mkv","flv","wmv","mov","webm","mpeg","3gp","3g2","flv","m4v","h264","h265","rm","rmvb","vob","ts","swf","avchd","m2ts","mxf","divx","xvid","f4v","asf","amv","dav","mpg","ogv","qt","yuv","rm","rmvb","vob","ts","swf","avchd","m2ts","mxf","divx","xvid","f4v","asf","amv","dav","mpg","ogv","qt","yuv","rm","rmvb","vob","ts","swf","avchd","m2ts","mxf","divx","xvid","f4v","asf","amv","dav","mpg","ogv","qt","yuv","rm","rmvb","vob","ts","swf","avchd","m2ts","mxf","divx","xvid","f4v","asf","amv","dav","mpg","ogv","qt","yuv","rm","rmvb","vob","ts","swf","avchd","m2ts","mxf","divx","xvid","f4v","asf","amv","dav","mpg","ogv","qt","yuv","rm","rmvb","vob","ts","swf","avchd","m2ts","mxf","divx","xvid","f4v","asf","amv","dav","mpg","ogv","qt","yuv","rm","rmvb","vob","ts","swf","avchd","m2ts","mxf","divx","xvid","f4v","asf","amv","dav","mpg","ogv","qt","yuv","rm","rmvb","vob","ts","swf","avchd","m2ts","mxf","divx","xvid","f4v","asf","amv","dav","mpg","ogv","qt","yuv","rm","rmvb","vob","ts","swf","avchd","m2ts","mxf","divx","xvid","f4v","asf","amv","dav","mpg","ogv","qt","yuv"))
                                .createdAt(now)
                                .build()
                );
            }
            if (fileTypeRepository.findByName(FileEnum.AUDIO.getId()).isEmpty()) {
                fileTypeRepository.save(
                        FileType.builder()
                                .id(FileEnum.AUDIO.getId())
                                .name(FileEnum.AUDIO.getName())
                                .extension(List.of("mp3", "wav", "ogg"))
                                .createdAt(now)
                                .build()
                );
            }
            if (fileTypeRepository.findById(FileEnum.DOCUMENT.getId()).isEmpty()) {
                fileTypeRepository.save(
                        FileType.builder()
                                .id(FileEnum.DOCUMENT.getId())
                                .name(FileEnum.DOCUMENT.getName())
                                .extension(List.of("doc", "docx", "pdf"))
                                .createdAt(now)
                                .build()
                );
            }
        };
    }
}