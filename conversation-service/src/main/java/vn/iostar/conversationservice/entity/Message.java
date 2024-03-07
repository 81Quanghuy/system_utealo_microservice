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
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "messages")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Message implements Serializable {

    @Id
    @Field(name = "message_id")
    private String id;

    @DocumentReference
    @Field(name = "chat_group_id")
    private ChatGroup chatGroup;

    @DocumentReference
    @Field(name = "sender_id")
    private ReactMessage sender;

    @DocumentReference
    @Field(name = "receiver_id")
    private ReactMessage receiver;

    @Field(name = "content")
    private String content;

    @Field(name = "files_url")
    private String filesUrl;
    @Builder.Default
    @Field(name = "is_deleted")
    private Boolean isDeleted = false;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
