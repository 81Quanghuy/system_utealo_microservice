package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResetPasswordRequest {

    @NotBlank(message = "Mật khẩu không được để trống!")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống!")
    private String confirmPassword;

}
