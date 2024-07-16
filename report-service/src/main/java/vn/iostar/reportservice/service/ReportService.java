package vn.iostar.reportservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.reportservice.dto.CreatePostRequestDTO;
import vn.iostar.reportservice.dto.GenericResponseAdmin;
import vn.iostar.reportservice.entity.Report;

import java.util.Optional;

public interface ReportService {
    <S extends Report> S save(S entity);
    Optional<Report> findById(String id);
    ResponseEntity<Object> createUserReport(String token, CreatePostRequestDTO requestDTO);
    ResponseEntity<Object> getReportById(String currentUserId, String reportId);
    // Lấy tất cả bài report trong hệ thống
    ResponseEntity<GenericResponseAdmin> getAllReports(String authorizationHeader, int page, int itemsPerPage);
    // Đã đọc report
    ResponseEntity<Object> readReport(String reportId);
}
