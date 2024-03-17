package vn.iostar.groupservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Builder
public class GroupCreateRequest {

    @NotNull(message = "name is required")
    private String postGroupName;
    private String bio;
    @NotNull(message = "isClass is required")
    private Boolean isSystem ;
    private Set<String> userRequestId;
    @NotNull(message = "isPublic is required")
    private Boolean isPublic;
    @NotNull(message = "isAcceptAllRequest is required")
    private Boolean isApprovalRequired;
    private Boolean isActive ;

    @Nationalized
    private MultipartFile avatar;

    @Nationalized
    private MultipartFile background;

}
