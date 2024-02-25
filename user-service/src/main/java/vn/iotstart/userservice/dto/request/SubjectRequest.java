package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubjectRequest {

    @NotNull(message = "Code must not be null")
    private String code;
    @NotNull(message = "Name must not be null")
    private String name;
    private String description;

}
