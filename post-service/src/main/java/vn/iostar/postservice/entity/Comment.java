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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Comment implements Serializable {
    @Id
    @Field(name = "comment_id")
    private String id;

    @DocumentReference
    @Field(name = "post_id")
    private Post post;

    @DocumentReference
    @Field(name = "share_id")
    private Share share;

    @Field(name = "content")
    private String content;

    @Field(name = "photos")
    private String photos;

    @Field(name = "user_id")
    private String userId;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "create_time")
    private Date createTime;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

    @DocumentReference
    @Field(name = "sub_comments")
    private List<Comment> subComments;

    @DocumentReference
    @Field(name = "comment_reply")
    private Comment commentReply;

    @Field(name = "likes")
    private List<String> likes;

}
