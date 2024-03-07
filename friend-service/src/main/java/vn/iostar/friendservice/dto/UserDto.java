package vn.iostar.friendservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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
    @JsonProperty("credential")
    private CredentialDto credentialDto;
    private Date createdAt;
    private Date updatedAt;

}
