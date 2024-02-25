package vn.iotstart.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmailRequest {

    @NotEmpty(message = "Email là bắt buộc!")
    @Email(message = "Email không hợp lệ!")
    private String email;
  
}
