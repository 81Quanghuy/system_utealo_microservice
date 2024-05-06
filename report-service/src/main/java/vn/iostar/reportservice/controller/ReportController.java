package vn.iostar.reportservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.reportservice.dto.CreatePostRequestDTO;
import vn.iostar.reportservice.jwt.service.JwtService;
import vn.iostar.reportservice.service.ReportService;

@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    @Autowired
    ReportService reportService;

    @Autowired
    JwtService jwtTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<Object> createUserReport(@ModelAttribute CreatePostRequestDTO requestDTO,
                                                   @RequestHeader("Authorization") String token) {
        return reportService.createUserReport(token, requestDTO);
    }

    // Lấy report theo Id
    @GetMapping("/get/{reportId}")
    public ResponseEntity<Object> getReportById(@RequestHeader("Authorization") String authorizationHeader,
                                                @PathVariable("reportId") String reportId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtTokenProvider.extractUserId(token);
        return reportService.getReportById(currentUserId, reportId);
    }

}
