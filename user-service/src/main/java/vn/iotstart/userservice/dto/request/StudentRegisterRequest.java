package com.trvankiet.app.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentRegisterRequest {

    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String gender;
    private String phone;
    @NotNull
    private String dob;
    private String province;
    private String district;
    private String school;
    @Min(1)
    @Max(12)
    private Integer grade;

}
