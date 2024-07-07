package vn.iostar.groupservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import vn.iostar.constant.RoleName;
import vn.iostar.groupservice.entity.AbstractMappedEntity;

import java.io.Serializable;

@Getter
@Setter
@Document(indexName = "groups")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupDocument extends AbstractMappedEntity implements Serializable {
    @Id
    private String id;
    @CompletionField
    @Field(type = FieldType.Text)
    private String postGroupName;

    private String bio;
    private String authorId;
    private Boolean isSystem;
    private Boolean isActive;
    private Boolean isPublic;
    private Boolean isApprovalRequired;
    private String backgroundGroup;
    private String avatarGroup;
}
