package com.trvankiet.app.dto.request;

import com.trvankiet.app.constant.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserInfoRequest {

    @NotBlank(message = "Tên là bắt buộc!")
    private String firstName;

    @NotBlank(message = "Họ là bắt buộc!")
    private String lastName;
    @NotNull
    private String phone;
    @NotNull
    private String dob;
    @NotNull
    private String gender;
    private String province;
    private String district;
    private String school;
    private List<String> subjects;

}
