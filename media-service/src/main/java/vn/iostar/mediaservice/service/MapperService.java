package vn.iostar.mediaservice.service;


import vn.iostar.mediaservice.dto.FileDto;
import vn.iostar.mediaservice.entity.File;

public interface MapperService {
    FileDto mapToFileDto(File file);
}
