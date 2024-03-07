package vn.iostar.groupservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@Data
public class UserDto implements Serializable {

    private String id;
    private String firstName;
    private String lastName;
    private String role;
    private String gender;
    private String email;
    private String phone;
    private Date dob;
    private String avatarUrl;
    private String coverUrl;
    @JsonIgnore
    @JsonProperty("credential")
    private CredentialDto credentialDto;

    private String district;
    private String province;
    private String school;

    private Integer grade;
    List<String> subjects;

    List<AnotherUserDto> parents;
    List<AnotherUserDto> children;

    private Date createdAt;
    private Date updatedAt;

}
