package vn.iostar.scheduleservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.scheduleservice.dto.request.FileRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.jwt.service.JwtService;
import vn.iostar.scheduleservice.service.ScheduleService;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule/admin")
public class ScheduleManagerController {

    private final ScheduleService scheduleService;
    private final JwtService jwtService;


    // Tạo thời khóa biểu
    @PostMapping("/create")
    public ResponseEntity<Object> createSchedule(@RequestBody ScheduleRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return scheduleService.createSchedule(currentUserId, requestDTO);
    }


    // Cập nhật thời khóa biểu
    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<Object> updateSchedule(@PathVariable String scheduleId, @RequestBody ScheduleRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return scheduleService.updateSchedule(scheduleId, requestDTO, currentUserId);
    }

    // Lấy tất cả thời khóa biểu trong hệ thống
    @GetMapping("/list")
    public ResponseEntity<Object> getAllSchedules(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
        return scheduleService.getAllSchedules(page, items);
    }

    // Import thời khóa biểu từ file excel
    @PostMapping("/uploadExcel")
    public ResponseEntity<Object> importSchedule(@ModelAttribute FileRequest fileRequest) throws IOException, ParseException {
        return scheduleService.importSchedule(fileRequest);
    }
}
