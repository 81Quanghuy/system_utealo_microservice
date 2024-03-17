package vn.iostar.groupservice.dto.request;

import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostGroupRequest {
    private String postGroupId;
    private String postGroupName;
    private String bio;
    private String userId;
    private Boolean isPublic;// true: private, false: public
    private Boolean isApprovalRequired;
    private Boolean isActive;

    @Nationalized
    private MultipartFile avatar;

    @Nationalized
    private MultipartFile background;
}
