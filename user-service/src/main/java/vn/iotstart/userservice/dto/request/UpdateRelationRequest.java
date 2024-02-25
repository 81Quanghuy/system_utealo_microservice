package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRelationRequest {

    @NotNull(message = "isAccepted is required")
    private Boolean isAccepted;

}
