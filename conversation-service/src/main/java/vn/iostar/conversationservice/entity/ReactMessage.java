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
import vn.iostar.conversationservice.constant.React;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "react_messages")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReactMessage implements Serializable {

    @Id
    @Field(name = "react_id")
    private String id;

    @DocumentReference
    @Field(name = "message_id")
    private Message message;

    @Field(name = "user_id")
    private String user_id;

    @Field(name = "react")
    private React react;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
