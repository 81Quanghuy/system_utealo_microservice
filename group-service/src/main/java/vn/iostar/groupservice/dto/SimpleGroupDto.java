package vn.iostar.groupservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SimpleGroupDto implements Serializable {

    private String id;
    private String name;
    private String description;
    private String avatarUrl;
    private String coverUrl;
    private Boolean isPublic;

}
