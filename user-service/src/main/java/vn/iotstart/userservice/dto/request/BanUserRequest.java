package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BanUserRequest {

    @NotNull
    private String userId;
    @NotNull
    private String reason;
}
