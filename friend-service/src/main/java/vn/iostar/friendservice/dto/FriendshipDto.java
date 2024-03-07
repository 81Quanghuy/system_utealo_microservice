package vn.iostar.friendservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class FriendshipDto implements Serializable {

    private String id;
    private String authorId;
    private List<String> friendIds;
    private String createdAt;
    private String updatedAt;

}
