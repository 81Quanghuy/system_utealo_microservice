package vn.iostar.groupservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupCreateRequest {

    @NotNull(message = "name is required")
    private String name;
    private String description;
    @NotNull(message = "isClass is required")
    private Boolean isClass;
    @NotNull(message = "isPublic is required")
    private Boolean isPublic;
    @NotNull(message = "isAcceptAllRequest is required")
    private Boolean isAcceptAllRequest;

}
