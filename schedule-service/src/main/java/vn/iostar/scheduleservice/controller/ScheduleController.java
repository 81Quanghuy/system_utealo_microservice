package vn.iostar.scheduleservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.scheduleservice.dto.GenericResponse;
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
                                                             @PathVariable("userId") String userId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size)
            throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        return scheduleService.getScheduleofOtherUser(currentUserId,userId, pageable);
    }
}
