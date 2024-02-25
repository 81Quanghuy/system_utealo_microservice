package com.trvankiet.app.dto.request;

import com.trvankiet.app.constant.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProfileRequest implements Serializable {

    @NotNull(message = "Tên là bắt buộc!")
    private String firstName;
    @NotNull(message = "Họ là bắt buộc!")
    private String lastName;
    @NotNull
    private String phone;
    @NotNull
    private String dob;
    @NotNull
    private String gender;

}
