package vn.iotstart.userservice.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;
import vn.iotstart.userservice.dto.AccountDto;

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
    @JsonProperty("account")
    private AccountDto accountDto;

    private Integer grade;
    List<String> subjects;

    List<AnotherUserDto> parents;
    List<AnotherUserDto> children;

    private Date createdAt;
    private Date updatedAt;

}
