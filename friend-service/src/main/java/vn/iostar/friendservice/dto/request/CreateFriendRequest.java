package vn.iostar.friendservice.dto.request;

import org.jetbrains.annotations.NotNull;
import lombok.Data;

@Data
public class CreateFriendRequest {

    @NotNull
    private String userId;

}
