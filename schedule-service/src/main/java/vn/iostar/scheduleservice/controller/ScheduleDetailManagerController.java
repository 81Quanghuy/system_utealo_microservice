package vn.iostar.scheduleservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.scheduleservice.dto.GenericResponse;
import vn.iostar.scheduleservice.dto.request.AddScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.FileRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.dto.response.GenericResponseAdmin;
import vn.iostar.scheduleservice.jwt.service.JwtService;
import vn.iostar.scheduleservice.service.ScheduleService;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scheduleDetail/admin")
public class ScheduleDetailManagerController {

    private final ScheduleService scheduleService;
    private final JwtService jwtService;


    // Tạo thời khóa biểu chi tiết
    @PostMapping("/create")
    public ResponseEntity<Object> createScheduleDetail(@RequestBody ScheduleDetailRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return scheduleService.createScheduleDetail(currentUserId, requestDTO);
    }

    // Thêm ScheduleDetial vào Schedule
    @PostMapping("/addScheduleDetail")
    public ResponseEntity<Object> addScheduleDetailtoSchdule(@RequestBody AddScheduleDetailRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return scheduleService.addScheduleDetailtoSchdule(currentUserId, requestDTO);
    }

    // Cập nhật thời khóa biểu chi tiết
    @PutMapping("/update/{scheduleDetailId}")
    public ResponseEntity<Object> updateScheduleDetail(@PathVariable String scheduleDetailId, @RequestBody ScheduleDetailRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return scheduleService.updateScheduleDetail(scheduleDetailId, requestDTO, currentUserId);
    }

    // Import thời khóa biểu chi tiết từ file excel
    @PostMapping("/uploadExcel")
    public ResponseEntity<Object> importScheduleDetail(@ModelAttribute FileRequest fileRequest) throws IOException, ParseException {
        return scheduleService.importScheduleDetails(fileRequest);
    }

    // Lấy tất cả thời khóa biểu chi tiết trong hệ thống có phân
    @GetMapping("/list")
    public ResponseEntity<GenericResponseAdmin> getAllScheduleDetails(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
        return scheduleService.getAllScheduleDetails(page, items);
    }

    // Xóa môn học ra khỏi Schedule
    @DeleteMapping("/delete/{scheduleDetailId}")
    public ResponseEntity<Object> deleteScheduleDetail(@PathVariable String scheduleDetailId) throws Exception {
        return scheduleService.deleteScheduleDetail(scheduleDetailId);
    }

}
