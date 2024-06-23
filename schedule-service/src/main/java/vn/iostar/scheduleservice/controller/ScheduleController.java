package vn.iostar.scheduleservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.scheduleservice.dto.GenericResponse;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.jwt.service.JwtService;
import vn.iostar.scheduleservice.service.ScheduleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtService jwtService;
    // Lấy thời khóa biểu của chính mình
    @GetMapping("/getMySchedule")
    public ResponseEntity<GenericResponse> getPostsByUserAndFriendsAndGroups(@RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        return scheduleService.getMySchedule(currentUserId, pageable);
    }

    // Tạo thời khóa biểu
    @PostMapping("/create")
    public ResponseEntity<Object> createSchedule(@ModelAttribute ScheduleRequest requestDTO, @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return scheduleService.createSchedule(currentUserId, requestDTO);
    }

}
