package vn.iostar.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GroupElastic extends AbstractMappedEntity implements Serializable {
    private String id;
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
