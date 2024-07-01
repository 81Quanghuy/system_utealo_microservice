package vn.iostar.userservice.dto.request;

import lombok.Data;
import vn.iostar.constant.RoleName;


@Data
public class UserManagerRequest {

	private String userId;
    private Boolean isActive;
    private RoleName roleName;
    
}
