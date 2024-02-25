package vn.iotstart.userservice.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import vn.iotstart.userservice.constant.RoleName;
import vn.iotstart.userservice.dto.AccountDto;

import java.util.Date;
import java.util.List;

@Builder
@Data
public class AnotherUserDto {

    private String id;
    private String firstName;
    private String lastName;
    private RoleName role;
    private String gender;
    private String email;
    private String phone;
    private Date dob;

    @JsonProperty("account")
    private AccountDto accountDto;

    private Date createdAt;
    private Date updatedAt;

}
