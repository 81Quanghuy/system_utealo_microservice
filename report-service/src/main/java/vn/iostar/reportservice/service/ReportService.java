package vn.iostar.reportservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.reportservice.dto.CreatePostRequestDTO;
import vn.iostar.reportservice.entity.Report;

import java.util.Optional;

public interface ReportService {
    <S extends Report> S save(S entity);
    Optional<Report> findById(String id);
    ResponseEntity<Object> createUserReport(String token, CreatePostRequestDTO requestDTO);
    ResponseEntity<Object> getReportById(String currentUserId, String reportId);
}
