package vn.iostar.groupservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupConfigRequest {
;
    @NotNull(message = "accessibilityCode is required")
    private Boolean isPublic;
    @NotNull(message = "isAcceptAllRequest is required")
    private Boolean isAcceptAllRequest;

}
