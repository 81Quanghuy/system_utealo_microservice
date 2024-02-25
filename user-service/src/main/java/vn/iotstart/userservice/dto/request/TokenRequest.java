package vn.iotstart.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRequest {

    private String accessToken;

    @NotBlank(message = "Refresh token là bắt buộc!")
    private String refreshToken;

}
