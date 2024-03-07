package vn.iostar.groupservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class CredentialDto implements Serializable {

    private String username;
    @JsonIgnore
    private String password;
    private Boolean isEnabled;
    private Boolean isAccountNonExpired;
    private Boolean isAccountNonLocked;
    private Boolean isCredentialsNonExpired;
    private Date lockedAt;
    private String lockedReason;
    private String role;
    private String provider;
    private Date createdAt;
    private Date updatedAt;

}
