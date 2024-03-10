package vn.iostar.userservice.dto.request;

import lombok.Data;
import vn.iostar.userservice.constant.RoleName;


@Data
public class UserManagerRequest {

	private String userId;
    private Boolean isActive;
    private RoleName roleName;
    
}
