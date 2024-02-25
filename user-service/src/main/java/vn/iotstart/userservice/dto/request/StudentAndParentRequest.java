package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentAndParentRequest {

    @NotNull
    private StudentRegisterRequest student;
    private ParentRegisterRequest parent;

}
