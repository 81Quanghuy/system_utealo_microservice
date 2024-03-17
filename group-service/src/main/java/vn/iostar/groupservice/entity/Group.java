package vn.iostar.groupservice.entity;

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

import java.io.Serializable;
import java.util.Date;

@Document(collection = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Group implements Serializable {

    @Id
    @Field(name = "group_id")
    private String id;

    @Field(name = "group_name")
    private String postGroupName;

    @Field(name = "bio")
    private String bio;

    @Field(name = "author_id")
    private String authorId;

    @Builder.Default
    @Field(name = "group_avatar_url")
    private String avatarGroup = "https://res.cloudinary.com/djzwxw0ao/image/upload/v1696942528/uqbxidtwcdbqn8glt6we.jpg";

    @Builder.Default
    @Field(name = "group_cover_url")
    private String backgroundGroup = "https://res.cloudinary.com/djzwxw0ao/image/upload/v1696942528/uqbxidtwcdbqn8glt6we.jpg";

    @Field(name = "isSystem")
    private Boolean isSystem;

    @Field(name = "isActive")
    private Boolean isActive;

    // true: private, false: public
    @Field(name = "isPublic")
    private Boolean isPublic;

    // Yêu cầu phê duyệt
    @Field(name = "isApprovalRequired")
    private Boolean isApprovalRequired;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
