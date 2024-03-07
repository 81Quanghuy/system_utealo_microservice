package vn.iostar.groupservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class AnotherUserDto {

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
