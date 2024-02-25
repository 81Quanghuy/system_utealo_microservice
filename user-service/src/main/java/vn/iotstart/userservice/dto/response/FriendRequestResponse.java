package vn.iotstart.userservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trvankiet.app.dto.SimpleUserDto;
import com.trvankiet.app.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendRequestResponse {

    private String id;
    @JsonProperty("sender")
    private SimpleUserDto userDto;
    private String status;
    private String createdAt;
    private String updatedAt;

}
