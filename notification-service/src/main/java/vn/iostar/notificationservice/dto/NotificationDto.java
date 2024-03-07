package vn.iostar.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;
import vn.iostar.notificationservice.constant.NotificationType;

import java.util.Date;

@Builder
@Getter
@Setter
public class NotificationDto {
    private String id;
    private String link;

    private String content;
    private String photo;
    private Boolean isRead;

    private String userId;
    private NotificationType type;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
