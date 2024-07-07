package vn.iostar.postservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import vn.iostar.model.AbstractMappedEntity;

import java.io.Serializable;

@Getter
@Setter
@Document(indexName = "posts")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDocument extends AbstractMappedEntity implements Serializable {
    @Id
    private String id;

    private String photos;
    private String files;
    private String video;
    @CompletionField
    @Field(type = FieldType.Text)
    private String location;
    @CompletionField
    @Field(type = FieldType.Text)
    private String content;
    private String privacyLevel;
    private String userId;
    private String groupId;
}
