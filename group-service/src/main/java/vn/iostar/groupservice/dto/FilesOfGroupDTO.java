package vn.iostar.groupservice.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilesOfGroupDTO  {
    private String userId;
    private String userName;
    private String files;
    private String postId;
    private String type;
    private Date createAt;
    private Date updateAt;
}