package vn.iostar.reportservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iostar.reportservice.entity.Report;
import vn.iostar.reportservice.service.ReportService;

@RestController
@RequestMapping("/api/v1/share")
public class ReportController {

    @Autowired
    ReportService reportService;

    @PostMapping("/create")
    public ResponseEntity<Report> createReport() {
        return ResponseEntity.ok(reportService.createReport());
    }
}
