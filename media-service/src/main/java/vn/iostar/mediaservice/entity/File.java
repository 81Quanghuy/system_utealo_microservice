package vn.iostar.mediaservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "files")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class File implements Serializable {

    @Id
    @Field(name = "file_id")
    private String id;

    @Field(name = "author_id")
    private String authorId;

    @Field(name = "group_id")
    private String groupId;

    @Field(name = "file_ref_url")
    private String refUrl;

    @DocumentReference
    @Field(name = "file_type")
    private FileType type;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;

}
