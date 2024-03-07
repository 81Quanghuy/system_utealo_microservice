package vn.iostar.friendservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateFriendRequest {

    @NotNull
    private Boolean isAccepted;

}
