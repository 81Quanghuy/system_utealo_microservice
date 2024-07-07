package vn.iostar.model;

import lombok.Getter;
import lombok.Setter;
import vn.iostar.constant.RoleName;

import java.io.Serializable;

@Getter
@Setter

public class UserElastic extends AbstractMappedEntity implements Serializable {
    private String userId;
    private String userName;
    private String phone;
    private Boolean isActive;
    private Boolean isOnline;
    private Boolean isVerified;
    private RoleName roleName;

    private String background;
    private String avatar;
    private String email;
}
