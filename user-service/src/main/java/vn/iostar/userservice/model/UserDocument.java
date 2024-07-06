package vn.iostar.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import vn.iostar.constant.RoleName;
import vn.iostar.userservice.entity.AbstractMappedEntity;

import java.io.Serializable;

@Getter
@Setter
@Document(indexName = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDocument extends AbstractMappedEntity implements Serializable {
    @Id
    private String userId;
    @CompletionField
    @Field(type = FieldType.Text)
    private String userName;

    @CompletionField
    @Field(type = FieldType.Text)
    private String phone;
    private Boolean isActive;
    private Boolean isOnline;
    private Boolean isVerified;
    private RoleName roleName;

    private String background;
    private String avatar;

    @CompletionField
    @Field(type = FieldType.Text)
    private String email;
}
