package vn.iostar.groupservice.dto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInviteGroup {
    private String userId;
    private String userName;
}