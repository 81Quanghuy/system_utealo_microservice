package vn.iostar.postservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import vn.iostar.postservice.constant.PrivacyLevel;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "shares")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Share implements Serializable {

    @Id
    @Field(name = "share_id")
    private String id;

    @Field(name = "content")
    private String content;

    @DocumentReference
    @Field(name = "post_id")
    private Post post;

    @Field(name = "post_group_id")
    private String postGroupId;

    @Field(name = "privacy_level")
    private PrivacyLevel privacyLevel;

    @Field(name = "user_id")
    private String userId;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "create_at")
    private Date createAt;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updateAt;

}
