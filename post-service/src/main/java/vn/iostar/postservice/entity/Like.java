package vn.iostar.postservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "likes")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Like implements Serializable {

    @Id
    @Field(name = "like_id")
    private String id;

    @DocumentReference
    @Field(name = "post_id")
    private Post post;

    @Field(name = "user_id")
    private String userId;

    @DocumentReference
    @Field(name = "share_id")
    private Share share;

    @DocumentReference
    @Field(name = "comment_id")
    private Comment comment;

    @Field(name = "status")
    private String status;

}
