package vn.iotstart.userservice.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    REFRESH_ACCESS_TOKEN("refresh_access_token"),
    VERIFICATION_TOKEN("verification_token"),
    RESET_PASSWORD_TOKEN("reset_password_token");

    private final String code;
}
