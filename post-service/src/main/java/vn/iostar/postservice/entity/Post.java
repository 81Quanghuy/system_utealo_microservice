package vn.iostar.postservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import vn.iostar.postservice.constant.PrivacyLevel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "posts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Post implements Serializable {

    @Id
    @Field(name = "post_id")
    private String id;

    @Field(name = "photos")
    private String photos;

    @Field(name = "files")
    private String files;

    @Field(name = "location")
    private String location;

    @Field(name = "content")
    private String content;

    @Field(name = "privacy_level")
    private PrivacyLevel privacyLevel;

    @Field(name = "user_id")
    private String userId;

    @Field(name = "group_id")
    private String groupId;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "post_time")
    private Date postTime;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

    @Field(name = "shares")
    private List<String> shares;

    @Field(name = "likes")
    private List<String> likes;

    @Field(name = "comments")
    private List<String> comments;

}
