package vn.iotstart.userservice.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {

    ROLE_USER("user"),
    ROLE_ADMIN("admin");

    private final String code;

}
