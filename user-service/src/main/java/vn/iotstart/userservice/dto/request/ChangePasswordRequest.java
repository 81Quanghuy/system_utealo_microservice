package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String confirmPassword;

}
