package vn.iostar.groupservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "group_requests")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupRequest implements Serializable {

    @Id
    @Field(name = "group_member_invitation_id")
    private String id;

    @DocumentReference
    @Field(name = "group_id")
    private Group group;

    @Field(name = "inviting_user_id")
    private String invitingUser; // nguời mời

    @Field(name = "invited_user_id")
    private String invitedUser; // nguời được mời

    @Field(name = "isAccept")
    private Boolean isAccept;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
