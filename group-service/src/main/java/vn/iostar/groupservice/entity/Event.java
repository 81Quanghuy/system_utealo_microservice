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

import java.io.Serializable;
import java.util.Date;

@Document(collection = "events")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Event implements Serializable {

    @Id
    @Field(name = "event_id")
    private String id;

    @DocumentReference
    @Field(name = "group_id")
    private Group group;

    @Field(name = "author_id")
    private String authorId;

    @Field(name = "event_title")
    private String title;

    @Field(name = "event_description")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "started_at")
    private Date startedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "ended_at")
    private Date endedAt;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
