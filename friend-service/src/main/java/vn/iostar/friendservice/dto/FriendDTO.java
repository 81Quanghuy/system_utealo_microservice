package vn.iostar.friendservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendDTO {
    private String id;
    private String userId;
    private String authorId;
    private List<String> friendIds;
    private Date createdAt;
    private Date updatedAt;
}
