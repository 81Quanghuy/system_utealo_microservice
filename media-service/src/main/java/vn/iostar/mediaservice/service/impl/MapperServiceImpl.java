package vn.iostar.mediaservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.iostar.mediaservice.constant.AppConstant;
import vn.iostar.mediaservice.dto.FileDto;
import vn.iostar.mediaservice.entity.File;
import vn.iostar.mediaservice.service.MapperService;
import vn.iostar.mediaservice.util.DateUtil;

@Service
@RequiredArgsConstructor
public class MapperServiceImpl implements MapperService {

    @Override
    public FileDto mapToFileDto(File file) {
        return FileDto.builder()
                .authorId(file.getAuthorId())
                .refUrl(file.getRefUrl())
                .type(file.getType().getName())
                .createdAt(DateUtil.date2String(file.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }
}
