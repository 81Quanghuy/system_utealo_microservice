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
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import vn.iostar.groupservice.constant.GroupMemberRoleType;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "group_members")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupMember implements Serializable {

    @Id
    @Field(name = "group_member_id")
    private String id;

    @Field(name = "user_id")
    private String userId;

    @Builder.Default
    @Field(name = "is_locked")
    private Boolean isLocked = false;

    @Field(name = "locked_at")
    private Date lockedAt;

    @Field(name = "locked_reason")
    private String lockedReason;

    @DocumentReference
    @Field(name = "group_id")
    private Group group;

    @Field(name = "group_member_role")
    private GroupMemberRoleType role;

    @Field(name = "group_member_request_id")
    private String memberRequestId; // nguoi moi vao nhom

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
