package vn.iostar.mediaservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.mediaservice.constant.AppConstant;
import vn.iostar.mediaservice.constant.FileEnum;
import vn.iostar.mediaservice.dto.FileDto;
import vn.iostar.mediaservice.dto.request.DeleteRequest;
import vn.iostar.mediaservice.dto.request.FileRequest;
import vn.iostar.mediaservice.dto.response.GenericResponse;
import vn.iostar.mediaservice.dto.response.ListMediaResponse;
import vn.iostar.mediaservice.entity.File;
import vn.iostar.mediaservice.entity.FileType;
import vn.iostar.mediaservice.exception.wrapper.BadRequestException;
import vn.iostar.mediaservice.exception.wrapper.NotFoundException;
import vn.iostar.mediaservice.exception.wrapper.UnsupportedMediaTypeException;
import vn.iostar.mediaservice.repository.FileRepository;
import vn.iostar.mediaservice.repository.FileTypeRepository;
import vn.iostar.mediaservice.service.CloudinaryService;
import vn.iostar.mediaservice.service.FileService;
import vn.iostar.mediaservice.service.MapperService;
import vn.iostar.mediaservice.util.DateUtil;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final CloudinaryService cloudinaryService;
    private final FileRepository fileRepository;
    private final FileTypeRepository fileTypeRepository;
    private final MapperService mapperService;

    @Override
    public List<FileDto> uploadPostFiles(String userId, List<MultipartFile> mediaFiles, String groupId) throws IOException {
        return this.uploadMediaFile(userId, mediaFiles, "/posts", groupId);
    }

    @Override
    public List<FileDto> uploadCommentFiles(String userId, List<MultipartFile> mediaFiles, String groupId) throws IOException {
        return this.uploadMediaFile(userId, mediaFiles, "/comments", groupId);
    }

    @Override
    public List<FileDto> uploadDocumentFiles(String userId, List<MultipartFile> mediaFiles, String groupId) throws IOException {
        return this.uploadMediaFile(userId, mediaFiles, "/documents", groupId);
    }

    @Override
    public ResponseEntity<GenericResponse> deletePostFiles(String userId, DeleteRequest deleteRequest) {
        return this.deleteMediaFiles(userId, deleteRequest, "/posts");
    }

    @Override
    public ResponseEntity<GenericResponse> deleteCommentFiles(String userId, DeleteRequest deleteRequest) {
        return this.deleteMediaFiles(userId, deleteRequest, "/comments");
    }

    @Override
    public ResponseEntity<GenericResponse> deleteDocumentFiles(String userId, DeleteRequest deleteRequest) {
        return this.deleteMediaFiles(userId, deleteRequest, "/documents");
    }
    @Override
    public String uploadUserAvatar(MultipartFile file) throws IOException {
        return this.uploadImage(file, "/users/avatars");
    }

    @Override
    public String uploadUserCover(MultipartFile file) throws IOException {
        return this.uploadImage(file, "/users/covers");
    }

    @Override
    public String uploadGroupAvatar(MultipartFile file) throws IOException {
        return this.uploadImage(file, "/groups/avatars");
    }

    @Override
    public String uploadGroupCover(MultipartFile file) throws IOException {
        return this.uploadImage(file, "/groups/covers");
    }

    @Override
    public void deleteUserAvatar(String refUrl) throws IOException {
        cloudinaryService.deleteImage(refUrl, "/users/avatars");
    }

    @Override
    public void deleteUserCover(String refUrl) throws IOException {
        cloudinaryService.deleteImage(refUrl, "/users/covers");
    }

    @Override
    public void deleteGroupAvatar(String refUrl) throws IOException {
        cloudinaryService.deleteImage(refUrl, "/groups/avatars");
    }

    @Override
    public void deleteGroupCover(String refUrl) throws IOException {
        cloudinaryService.deleteImage(refUrl, "/groups/covers");
    }

    @Override
    public ResponseEntity<GenericResponse> getUserImage(String userId, Integer page, Integer size) {
        log.info("FileServiceImpl, getUserImage");
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        List<FileDto> files = fileRepository.findAllByAuthorIdAndTypeId(userId, FileEnum.IMAGE.getId(), pageable)
                .stream()
                .map(mapperService::mapToFileDto)
                .toList();

        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .message("Get user image successfully")
                .result(files)
                .statusCode(200)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupImage(String userId, Integer page, Integer size) {
        log.info("FileServiceImpl, getGroupImage");

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        List<FileDto> files = fileRepository.findAllByGroupIdAndTypeIdIn(userId, List.of(FileEnum.IMAGE.getId(), FileEnum.VIDEO.getId()), pageable)
                .stream()
                .map(mapperService::mapToFileDto)
                .toList();

        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .message("Get group image successfully")
                .result(files)
                .statusCode(200)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupDocument(String userId, Integer page, Integer size) {
        log.info("FileServiceImpl, getGroupDocument");
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        List<FileDto> files = fileRepository.findAllByGroupIdAndTypeId(userId, FileEnum.DOCUMENT.getId(), pageable)
                .stream()
                .map(mapperService::mapToFileDto)
                .toList();

        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .message("Get group document successfully")
                .result(files)
                .statusCode(200)
                .build());
    }
    public FileDto uploadMessageImage(MultipartFile file, String userId) throws IOException{
        log.info("FileServiceImpl, uploadMessageImage");
        try {
            if (file.isEmpty() || file.getOriginalFilename() == null) {
                throw new UnsupportedMediaTypeException("File is null. Please upload a valid file.");
            }
            String fileName = file.getOriginalFilename();
            Date now = new Date();
            String name = DateUtil.date2String(now, AppConstant.FULL_DATE_TIME_FORMAT) + "_" + getFileName(fileName);
            File fileEntity = fileRepository.save(File.builder()
                    .id(UUID.randomUUID().toString())
                    .authorId(userId)
                    .size(file.getSize())
                    .name(name)
                    .isMessage(true)
                    .refUrl(Objects.requireNonNull(file.getContentType()).startsWith("image/") ? cloudinaryService.uploadImage(file, name, "/messages")
                            : cloudinaryService.uploadMediaFile(file, name, "/messages"))
                    .type(fileTypeRepository.findByExtensionContaining(getFileExtension(fileName))
                            .orElseThrow(() -> new NotFoundException("File type not found")))
                    .createdAt(now)
                    .build());
            return  mapperService.mapToFileDto(fileEntity);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Upload file failed");
        }
    }

    @Override
    public ResponseEntity<GenericResponse> uploadMessageImages(List<MultipartFile> files, String userId) throws IOException{
       List<FileDto> fileDtos = new ArrayList<>();
        for (MultipartFile file : files) {
            fileDtos.add(uploadMessageImage(file, userId));
        }
        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .message("Upload file successfully")
                .result(fileDtos)
                .statusCode(200)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getMessageImage(String mediaId) {
        log.info("FileServiceImpl, getMessageImage");
        Optional<File> file = fileRepository.findById(mediaId);
        if (file.isEmpty()) {
            throw new NotFoundException("File not found");
        }
        FileDto fileDto = mapperService.mapToFileDto(file.get());
        return ResponseEntity.ok().body(GenericResponse.builder()
                .success(true)
                .message("Get media successfully")
                .result(fileDto)
                .statusCode(200)
                .build());
    }

    @Override
    public Page<File> getMediaList(ListMediaResponse fileRequest, Pageable pageable) {
        log.info("FileServiceImpl, getMediaList");
        return fileRepository.findAllByIdInAndTypeIdInOrderByCreatedAtDesc(fileRequest.getMediaIds(), fileRequest.getType(),pageable);
    }

    public String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * @param userId     String
     * @param mediaFiles MultiparFile
     * @param folder     String "/folder"
     * @return List<FileDto>
     * @throws IOException IOException
     */
    public List<FileDto> uploadMediaFile(String userId, List<MultipartFile> mediaFiles,
                                         String folder, String groupId) throws IOException {
        List<FileDto> fileDtos = new ArrayList<>();
        for (MultipartFile mediaFile : mediaFiles) {
            if (mediaFile.getOriginalFilename() != null) {
                String fileName = mediaFile.getOriginalFilename();
                Date now = new Date();
                String name = DateUtil.date2String(now, AppConstant.FULL_DATE_TIME_FORMAT) + "_" + fileName;
                File file = fileRepository.save(File.builder()
                        .id(UUID.randomUUID().toString())
                        .authorId(userId)
                        .groupId(groupId)
                        .refUrl(cloudinaryService.uploadMediaFile(mediaFile, name, folder))
                        .type(fileTypeRepository.findByExtensionContaining(getFileExtension(fileName))
                                .orElseThrow(() -> new NotFoundException("File type not found")))
                        .createdAt(now)
                        .build());
                FileDto fileDto = mapperService.mapToFileDto(file);
                fileDtos.add(fileDto);
            }
        }
        return fileDtos;
    }

    public String uploadImage(MultipartFile mediaFile, String folder) throws IOException {
        if (mediaFile.isEmpty() || mediaFile.getOriginalFilename() == null) {
            throw new UnsupportedMediaTypeException("File is null. Please upload a valid file.");
        }
        String fileName = mediaFile.getOriginalFilename();
        Date now = new Date();
        String name = DateUtil.date2String(now, AppConstant.FULL_DATE_TIME_FORMAT) + "_" + getFileName(fileName);
        return cloudinaryService.uploadImage(mediaFile, name, folder);
    }

    public ResponseEntity<GenericResponse> deleteMediaFiles(String userId, DeleteRequest deleteRequest, String folder) {
        try {
            for (String url : deleteRequest.getRefUrls()) {
                File file = fileRepository.findByRefUrl(url)
                        .orElseThrow(() -> new NotFoundException("File not found"));
                if (!file.getAuthorId().equals(userId)) {
                    throw new BadRequestException("You are not the owner of this file");
                }
                cloudinaryService.deleteMediaFile(url, folder);
            }
            return ResponseEntity.ok().body(GenericResponse.builder()
                    .success(true)
                    .message("Delete file successfully")
                    .result(null)
                    .statusCode(200)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(GenericResponse.builder()
                    .success(false)
                    .message("Delete file failed")
                    .result(e.getMessage())
                    .statusCode(400)
                    .build());
        }
    }
}
