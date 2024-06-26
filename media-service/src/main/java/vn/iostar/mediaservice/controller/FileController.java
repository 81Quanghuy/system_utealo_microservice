package vn.iostar.mediaservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.mediaservice.dto.FileDto;
import vn.iostar.mediaservice.dto.request.DeleteRequest;
import vn.iostar.mediaservice.dto.request.FileRequest;
import vn.iostar.mediaservice.dto.response.GenericResponse;
import vn.iostar.mediaservice.dto.response.ListMediaResponse;
import vn.iostar.mediaservice.entity.File;
import vn.iostar.mediaservice.jwt.service.JwtService;
import vn.iostar.mediaservice.service.CloudinaryService;
import vn.iostar.mediaservice.service.FileService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final CloudinaryService cloudinaryService;
    private final JwtService jwtService;

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<FileDto> uploadPostFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @RequestPart("mediaFiles") List<MultipartFile> mediaFiles,
                                         @RequestPart("groupId") String groupId) throws IOException {
        log.info("FileController, uploadPostFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.uploadPostFiles(userId, mediaFiles, groupId);
    }

    @DeleteMapping("/posts")
    public ResponseEntity<GenericResponse> deletePostFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody DeleteRequest deleteRequest) {
        log.info("FileController, deleteFile");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.deletePostFiles(userId, deleteRequest);
    }

    @PostMapping(value = "/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<FileDto> uploadCommentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                            @RequestPart("mediaFiles") List<MultipartFile> mediaFiles,
                                            @RequestPart("groupId") String groupId) throws IOException {
        log.info("FileController, uploadCommentFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.uploadCommentFiles(userId, mediaFiles, groupId);
    }

    @DeleteMapping("/comments")
    public ResponseEntity<GenericResponse> deleteCommentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody DeleteRequest deleteRequest) {
        log.info("FileController, deleteCommentFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.deleteCommentFiles(userId, deleteRequest);
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<FileDto> uploadDocumentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                             @RequestPart("mediaFiles") List<MultipartFile> mediaFiles,
                                             @RequestPart("groupId") String groupId) throws IOException {
        log.info("FileController, uploadDocumentFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.uploadDocumentFiles(userId, mediaFiles, groupId);
    }

    @DeleteMapping("/documents")
    public ResponseEntity<GenericResponse> deleteDocumentFiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                      @RequestBody DeleteRequest deleteRequest) {
        log.info("FileController, deleteDocumentFiles");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.deleteDocumentFiles(userId, deleteRequest);
    }

    @PostMapping(value = "/uploadUserAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadUserAvatar(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return fileService.uploadUserAvatar(file);
    }

    @DeleteMapping(value = "/deleteUserAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void deleteUserAvatar(@RequestPart("refUrl") String refUrl) throws IOException {
        fileService.deleteUserAvatar(refUrl);
    }

    @PostMapping(value = "/uploadUserCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadUserCover(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return fileService.uploadUserCover(file);
    }

    @DeleteMapping(value = "/deleteUserCover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void deleteUserCover(@RequestPart("refUrl") String refUrl) throws IOException {
        fileService.deleteUserCover(refUrl);
    }

    @PostMapping(value = "/uploadGroupAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadGroupAvatar(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return fileService.uploadGroupAvatar(file);
    }

    @DeleteMapping("/deleteGroupAvatar")
    public void deleteGroupAvatar(@RequestPart("refUrl") String refUrl) throws IOException {
        fileService.deleteGroupAvatar(refUrl);
    }

    @PostMapping("/uploadPhoto")
    public String uploadPhoto(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return fileService.uploadGroupCover(file);
    }

    @DeleteMapping("/deleteGroupCover")
    public void deleteGroupCover(@RequestPart("refUrl") String refUrl) throws IOException {
        fileService.deleteGroupCover(refUrl);
    }

    @GetMapping("/getUserImage")
    public ResponseEntity<GenericResponse> getUserImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(value = "size", defaultValue = "4") Integer size) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.getUserImage(userId, page, size);
    }

    @GetMapping("/getGroupImage")
    public ResponseEntity<GenericResponse> getGroupImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(value = "size", defaultValue = "4") Integer size) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.getGroupImage(userId, page, size);
    }

    @GetMapping("/getGroupDocument")
    public ResponseEntity<GenericResponse> getGroupDocument(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                        @RequestParam(value = "size", defaultValue = "4") Integer size) {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.getGroupDocument(userId, page, size);
    }

    //Tai anh trong message
    @PostMapping(value = "/uploadMessageImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> uploadMessageImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader ,
            @RequestPart("files") List<MultipartFile> files) throws IOException {
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return fileService.uploadMessageImages(files, userId);

    }

    //Lay anh trong message
    @GetMapping("/getMedia/{mediaId}")
    public ResponseEntity<GenericResponse> getMessageImage(@PathVariable String mediaId) {
        return fileService.getMessageImage(mediaId);
    }
    //Get list media by list mediaId
    @PostMapping("/getMediaList")
    public Page<File> getMediaList(@RequestBody ListMediaResponse fileRequest,
                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam(value = "size", defaultValue = "4") Integer size){
        Pageable pageable = PageRequest.of(page, size);
        return fileService.getMediaList(fileRequest,pageable);
    }

    // Upload file
    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return cloudinaryService.uploadFile(file);
    }

    // Upload image
    @PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadImage(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return cloudinaryService.uploadImage(file);
    }

    // Upload video
    @PostMapping(value = "/uploadVideo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadVideo(@RequestPart("mediaFile") MultipartFile file) throws IOException {
        return cloudinaryService.uploadVideo(file);
    }
}
