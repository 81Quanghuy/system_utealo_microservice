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
    private int postId;
    private String type;
    private Date createAt;
    private Date updateAt;

    public FilesOfGroupDTO(String userId, String userName, String files, Integer postId, Date createAt, Date updateAt) {
        this.userId = userId;
        this.userName = userName;
        this.files = files;
        this.postId = postId;
        this.setCreateAt(createAt);
        this.setUpdateAt(updateAt);
    }
}