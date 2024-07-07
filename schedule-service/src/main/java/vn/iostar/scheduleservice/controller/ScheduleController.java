package vn.iostar.scheduleservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.scheduleservice.constant.RoleName;
import vn.iostar.scheduleservice.dto.GenericResponse;
import vn.iostar.scheduleservice.dto.request.AddScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.FileRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.dto.response.UserProfileResponse;
import vn.iostar.scheduleservice.jwt.service.JwtService;
import vn.iostar.scheduleservice.service.ScheduleService;
import vn.iostar.scheduleservice.service.client.UserClientService;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtService jwtService;
    // Lấy thời khóa biểu của chính mình
    @GetMapping("/getMySchedule")
    public ResponseEntity<GenericResponse> getMySchedule(@RequestHeader("Authorization") String authorizationHeader,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size)
            throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        return scheduleService.getSchedule(currentUserId, pageable);
    }

    // Lấy thời khóa biểu của người khác (Admin)
    @GetMapping("/getScheduleOfUser/{userId}")
    public ResponseEntity<GenericResponse> getScheduleOfUser(@RequestHeader("Authorization") String authorizationHeader,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size,
                                                             @PathVariable String userId)
            throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        return scheduleService.getScheduleofOtherUser(currentUserId,userId, pageable);
    }

    // Tạo thời khóa biểu
    @PostMapping("/create")
    public ResponseEntity<Object> createSchedule(@RequestBody ScheduleRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return scheduleService.createSchedule(currentUserId, requestDTO);
    }

    // Tạo thời khóa biểu chi tiết
    @PostMapping("/createScheduleDetail")
    public ResponseEntity<Object> createScheduleDetail(@RequestBody ScheduleRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
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

    // Cập nhật thời khóa biểu
    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<Object> updateSchedule(@PathVariable String scheduleId, @RequestBody ScheduleRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return scheduleService.updateSchedule(scheduleId, requestDTO, currentUserId);
    }

    // Cập nhật thời khóa biểu chi tiết
    @PutMapping("/updateScheduleDetail/{scheduleDetailId}")
    public ResponseEntity<Object> updateScheduleDetail(@PathVariable String scheduleDetailId, @RequestBody ScheduleDetailRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return scheduleService.updateScheduleDetail(scheduleDetailId, requestDTO, currentUserId);
    }

    // Import thời khóa biểu chi tiết từ file excel
    @PostMapping("/importScheduleDetails")
    public ResponseEntity<Object> importScheduleDetails(@ModelAttribute FileRequest fileRequest) throws IOException, ParseException {
        return scheduleService.importScheduleDetails(fileRequest);
    }
}
