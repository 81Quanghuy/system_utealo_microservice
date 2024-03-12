package vn.iostar.friendservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import vn.iostar.friendservice.constant.FriendStateEnum;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "friend_requests")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FriendRequest implements Serializable {

    @Id
    @Field(name = "friend_request_id")
    private String id;

    @Field(name = "sender_id")
    private String senderId;

    @Field(name = "recipient_id")
    private String recipientId;

    @Field(name = "is_active")
    private Boolean isActive;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
