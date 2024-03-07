package vn.iostar.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iostar.notificationservice.dto.NotificationDto;
import vn.iostar.notificationservice.dto.response.GenericResponse;
import vn.iostar.notificationservice.service.NotificationService;

@RestController
@Slf4j
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
   //create notification for user about the new post
    private final NotificationService notificationService;
    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createNotification(@RequestBody NotificationDto notificationDto) {
        log.info("Create notification");
         notificationService.createNotification(notificationDto);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Create notification successfully!")
                .result(null)
                .build());

    }


}
