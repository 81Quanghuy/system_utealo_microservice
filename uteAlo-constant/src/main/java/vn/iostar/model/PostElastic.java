package vn.iostar.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.iostar.constant.RoleName;

import java.io.Serializable;

@Getter
@Setter

public class PostElastic extends AbstractMappedEntity implements Serializable {
    @Id
    private String id;
    private String photos;
    private String files;
    private String video;
    private String location;
    private String content;
    private String privacyLevel;
    private String userId;
    private String postGroupId;
}
