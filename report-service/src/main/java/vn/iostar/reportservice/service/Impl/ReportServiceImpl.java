package vn.iostar.reportservice.service.Impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.iostar.reportservice.dto.*;
import vn.iostar.reportservice.entity.Report;
import vn.iostar.reportservice.jwt.service.JwtService;
import vn.iostar.reportservice.repository.ReportRepository;
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
        post.setShareId(requestDTO.getShareId());
        post.setPostId(requestDTO.getPostId());
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

    @Override
    public ResponseEntity<GenericResponseAdmin> getAllReports(String authorizationHeader, int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<Report> schedulesPage = reportRepository.findAll(pageable);
        long totalSchedules = reportRepository.count();

        PaginationInfo pagination = new PaginationInfo();
        pagination.setPage(page);
        pagination.setItemsPerPage(itemsPerPage);
        pagination.setCount(totalSchedules);
        pagination.setPages((int) Math.ceil((double) totalSchedules / itemsPerPage));

        if (schedulesPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder()
                    .success(true)
                    .message("Empty")
                    .result(null)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(GenericResponseAdmin.builder()
                    .success(true)
                    .message("Lấy danh sách thời khóa biểu thành công")
                    .result(schedulesPage)
                    .pagination(pagination)
                    .statusCode(HttpStatus.OK.value())
                    .build());
        }
    }

    @Override
    public ResponseEntity<Object> readReport(String reportId) {
        Optional<Report> reportOptional = reportRepository.findById(reportId);
        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            report.setIsRead(true);
            reportRepository.save(report);
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Read report successfully")
                    .result(report).statusCode(HttpStatus.OK.value()).build());
        }
        throw new NotFoundException("Report not found");
    }
}
