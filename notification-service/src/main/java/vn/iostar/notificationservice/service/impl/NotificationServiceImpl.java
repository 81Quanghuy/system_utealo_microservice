package vn.iostar.notificationservice.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import vn.iostar.notificationservice.dto.NotificationDto;
import vn.iostar.notificationservice.entity.Notification;
import vn.iostar.notificationservice.repository.NotificationRepository;
import vn.iostar.notificationservice.service.NotificationService;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository emailRepository;

    @Override
    public void createNotification(NotificationDto notificationDto) {
        log.info("Create notification");
        Notification notification = Notification.builder()
                .link(notificationDto.getLink())
                .content(notificationDto.getContent())
                .photo(notificationDto.getPhoto())
                .isRead(false)
                .userId(notificationDto.getUserId())
                .id(UUID.randomUUID().toString())
                .type(notificationDto.getType())
                .build();
        emailRepository.save(notification);

    }
}
