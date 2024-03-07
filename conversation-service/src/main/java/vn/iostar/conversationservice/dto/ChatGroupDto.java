package vn.iostar.conversationservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.iostar.conversationservice.entity.ChatUser;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
public class ChatGroupDto {

    private String id;

    private String authorId;

    private String name;

    private String avatarUrl;

    private List<ChatUser> members;

    private Boolean isAcceptAllRequest;

    private Date createdAt;
    private Date updatedAt;
}
