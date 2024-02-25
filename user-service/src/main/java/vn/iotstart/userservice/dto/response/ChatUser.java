package vn.iotstart.userservice.dto.response;

import lombok.Builder;
import lombok.Data;
import vn.iotstart.userservice.constant.StatusEnum;

import java.util.Date;
@Data
@Builder
public class ChatUser  {
    private String id;

    private String firstName;
    private String lastName;
    private String avatarUrl;
    private StatusEnum status;
    private Date lastOnline;

}
