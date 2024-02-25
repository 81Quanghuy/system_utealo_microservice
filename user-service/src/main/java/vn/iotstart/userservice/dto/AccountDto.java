package vn.iotstart.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class AccountDto implements Serializable {

    private String accountId;
    private String email;

    @JsonIgnore
    private String password;
    private Boolean isActive;
    private Boolean isVerified ;
    private Date lastLoginAt;
    private Date lockedAt;
    private String lockedReason;

    private Date createdAt;
    private Date updatedAt;
}
