package vn.iostar.reportservice.service.Impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.iostar.reportservice.dto.CreatePostRequestDTO;
import vn.iostar.reportservice.dto.GenericResponse;
import vn.iostar.reportservice.dto.ReportsResponse;
import vn.iostar.reportservice.dto.UserProfileResponse;
import vn.iostar.reportservice.jwt.service.JwtService;
import vn.iostar.reportservice.repository.ReportRepository;
import vn.iostar.reportservice.entity.Report;
import vn.iostar.reportservice.service.CloudinaryService;
import vn.iostar.reportservice.service.ReportService;
import vn.iostar.reportservice.service.client.UserClientService;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final CloudinaryService cloudinaryService;
    private final JwtService jwtService;
    private final UserClientService userClientService;

    @Override
    public <S extends Report> S save(S entity) {
        return reportRepository.save(entity);
    }

    @Override
    public Optional<Report> findById(String id) {
        return reportRepository.findById(id);
    }

    @Override
    public ResponseEntity<Object> createUserReport(String token, CreatePostRequestDTO requestDTO) {
        List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");

        if (requestDTO.getLocation() == null && requestDTO.getContent() == null) {
            return ResponseEntity.badRequest().body("Please provide all required fields.");
        }

        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);

        Report post = new Report();
        post.setId(UUID.randomUUID().toString());
        post.setContent(requestDTO.getContent());
        post.setPrivacyLevel(requestDTO.getPrivacyLevel());

        try {
            if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
                post.setPhotos("");
            } else {
                post.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
            }
            if (requestDTO.getFiles() == null || requestDTO.getFiles().getContentType() == null) {
                post.setFiles("");
            } else {
                String fileExtension = StringUtils.getFilenameExtension(requestDTO.getFiles().getOriginalFilename());
                if (fileExtension != null && allowedFileExtensions.contains(fileExtension.toLowerCase())) {
                    post.setFiles(cloudinaryService.uploadFile(requestDTO.getFiles()));
                } else {
                    throw new IllegalArgumentException("Not support for this file.");
                }
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        } else {
            post.setUserId(user.getUserId());
        }

        // Thiết lập các giá trị cố định
        post.setPostTime(new Date());

        // Tiếp tục xử lý tạo bài đăng
        save(post);
        ReportsResponse postsResponse = new ReportsResponse(post, user);

        GenericResponse response = GenericResponse.builder().success(true).message("Report Created Successfully")
                .result(postsResponse).statusCode(200).build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> getReportById(String currentUserId, String reportId) {
        UserProfileResponse user = userClientService.getUser(currentUserId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        Optional<Report> repoOptional = reportRepository.findById(reportId);
        if (repoOptional.isPresent()) {
            ReportsResponse reportsResponse = new ReportsResponse(repoOptional.get(), user);

            return ResponseEntity
                    .ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
                            .result(reportsResponse).statusCode(HttpStatus.OK.value()).build());
        }
        throw new NotFoundException("Report not found");

    }
}
