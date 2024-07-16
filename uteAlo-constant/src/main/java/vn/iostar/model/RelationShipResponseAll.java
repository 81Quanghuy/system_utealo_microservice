package vn.iostar.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class RelationShipResponseAll {

    private String id;
    private String parentUserId;
    private String childUserId;
    private Boolean isAccepted;
    private String parentUserName;
    private String parentUserAvatar;
    private String childUserName;
    private String childUserAvatar;
    private String parentUserEmail;
    private String childUserEmail;

}
