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
import vn.iostar.conversationservice.constant.CallMessageType;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "call_message")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CallMessage implements Serializable {
    @Id
    @Field(name = "call_message_id")
    private String id;

    @Field(name = "call_message_type")
    private CallMessageType type;

    @Field(name = "caller_id")
    private String callerId;

    @Field(name = "receiver_id")
    private String receiverId;

    @Field(name = "duration")
    private Long duration;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
