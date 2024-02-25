package com.trvankiet.app.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateChatUserRequest {

    private String id;
    private String firstName;
    private String lastName;
    private String avatarUrl;

}
