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
    private int postGroupId;
    private String postGroupName;
    private int postId;
}
