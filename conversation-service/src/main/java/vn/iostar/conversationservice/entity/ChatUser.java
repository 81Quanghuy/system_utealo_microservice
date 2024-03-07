package vn.iostar.conversationservice.entity;

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
import vn.iostar.conversationservice.constant.StatusEnum;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "chat_users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatUser implements Serializable {

    @Id
    @Field(name = "user_id")
    private String id;

    @Field(name = "username")
    private String username;

    @Field(name = "avatar_url")
    private String avatarUrl;

    @Field(name = "status")
    private StatusEnum status;

    @Field(name = "last_online")
    private Date lastOnline;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
