package vn.iostar.reportservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.reportservice.dto.GenericResponseAdmin;
import vn.iostar.reportservice.service.ReportService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report/admin")
public class ReportManagerController {

    private final ReportService reportService;


    // Lấy tất cả bài report trong hệ thống
    @GetMapping("/list")
    public ResponseEntity<GenericResponseAdmin> getAllPosts(@RequestHeader("Authorization") String authorizationHeader,
                                                            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
        return reportService.getAllReports(authorizationHeader, page, items);
    }

    // Đã đọc report
    @PutMapping("/read/{reportId}")
    public ResponseEntity<Object> readReport(@PathVariable("reportId") String reportId) {
        return reportService.readReport(reportId);
    }
}
