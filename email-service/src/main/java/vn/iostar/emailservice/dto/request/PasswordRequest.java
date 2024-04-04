package vn.iostar.emailservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PasswordRequest {
    private String email;
    private String otp;

}
