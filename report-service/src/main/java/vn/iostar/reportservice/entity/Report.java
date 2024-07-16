package vn.iostar.reportservice.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import vn.iostar.reportservice.constant.PrivacyLevel;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "reports")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Report implements Serializable {
    @Id
    @Field(name = "report_id")
    private String id;

    @Field(name = "content")
    private String content;

    @Field(name = "files")
    private String files;

    @Field(name = "photos")
    private String photos;

    @Field(name = "privacy_level")
    private PrivacyLevel privacyLevel;

    @Field(name = "user_id")
    private String userId;

    @Field(name = "post_id")
    private String postId;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "post_time")
    private Date postTime;

    @Field(name = "share_id")
    private String shareId;

    @Builder.Default
    @Field(name = "isRead")
    private Boolean isRead =false;
}
