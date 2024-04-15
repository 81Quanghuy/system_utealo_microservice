package vn.iostar.groupservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotosOfGroupDTO {
    private String userId;
    private String userName;
    private String photos;
    private String postGroupId;
    private String postGroupName;
    private String postId;
}
