package vn.iostar.groupservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideosOfGroupDTO {
    private String userId;
    private String userName;
    private String video;
    private String postGroupId;
    private String postGroupName;
    private String postId;
}
