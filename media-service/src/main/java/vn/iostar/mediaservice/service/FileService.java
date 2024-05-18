package vn.iostar.mediaservice.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.mediaservice.dto.FileDto;
import vn.iostar.mediaservice.dto.request.DeleteRequest;
import vn.iostar.mediaservice.dto.request.FileRequest;
import vn.iostar.mediaservice.dto.response.GenericResponse;
import vn.iostar.mediaservice.dto.response.ListMediaResponse;
import vn.iostar.mediaservice.entity.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface FileService {
    List<FileDto> uploadPostFiles(String userId, List<MultipartFile> mediaFiles, String groupId) throws IOException;

    List<FileDto> uploadCommentFiles(String userId, List<MultipartFile> mediaFiles, String groupId) throws IOException;

    List<FileDto> uploadDocumentFiles(String userId, List<MultipartFile> mediaFiles, String groupId) throws IOException;

    ResponseEntity<GenericResponse> deletePostFiles(String userId, DeleteRequest deleteRequest);

    ResponseEntity<GenericResponse> deleteCommentFiles(String userId, DeleteRequest deleteRequest);

    ResponseEntity<GenericResponse> deleteDocumentFiles(String userId, DeleteRequest deleteRequest);

    String uploadUserAvatar(MultipartFile file) throws IOException;

    String uploadUserCover(MultipartFile file) throws IOException;

    String uploadGroupAvatar(MultipartFile file) throws IOException;

    String uploadGroupCover(MultipartFile file) throws IOException;

    void deleteUserAvatar(String refUrl) throws IOException;

    void deleteUserCover(String refUrl) throws IOException;

    void deleteGroupAvatar(String refUrl) throws IOException;

    void deleteGroupCover(String refUrl) throws IOException;

    ResponseEntity<GenericResponse> getUserImage(String userId, Integer page, Integer size);

    ResponseEntity<GenericResponse> getGroupImage(String userId, Integer page, Integer size);

    ResponseEntity<GenericResponse> getGroupDocument(String userId, Integer page, Integer size);

    ResponseEntity<GenericResponse> uploadMessageImages(List<MultipartFile> files, String userId) throws IOException;

    ResponseEntity<GenericResponse> getMessageImage(String mediaId);

    Page<File> getMediaList(ListMediaResponse fileRequest, Pageable pageable);
}
