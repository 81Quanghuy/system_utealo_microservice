package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {

    @NotEmpty(message = "Email là bắt buộc!")
    @Email(message = "Email không hợp lệ!")
    private String email;

    @NotEmpty(message = "Mật khẩu là bắt buộc!")
    private String password;

}
