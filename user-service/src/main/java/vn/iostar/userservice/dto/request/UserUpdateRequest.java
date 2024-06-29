package vn.iostar.userservice.dto.request;

import java.util.Date;

import org.hibernate.annotations.Nationalized;

import lombok.Data;
import vn.iostar.constant.Gender;


@Data
public class UserUpdateRequest {
    @Nationalized

    private String fullName;

    private String address;

    private Date dateOfBirth;
    
    @Nationalized
    private String phone;
    
    @Nationalized
    private Gender gender;

    @Nationalized
    private String about;

}
