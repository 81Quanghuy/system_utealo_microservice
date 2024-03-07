package vn.iostar.reportservice.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.iostar.reportservice.ReportRepository;
import vn.iostar.reportservice.constant.PrivacyLevel;
import vn.iostar.reportservice.entity.Report;
import vn.iostar.reportservice.service.ReportService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public Report createReport() {

        Report report = Report.builder()
                .id(UUID.randomUUID().toString())
                .content("Good Web")
                .privacyLevel(PrivacyLevel.CONTRIBUTE)
                .userId("1")
                .build();
        reportRepository.save(report);
        return report;
    }
}
