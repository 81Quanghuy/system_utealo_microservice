package vn.iostar.groupservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchPostGroup {
    private String postGroupId;
    private String postGroupName;
    private String avatarGroup;
    private String checkUserInGroup;
    private String bio;
    private Boolean isPublic;
    private int countMember;
    private int countFriendJoinnedGroup;

    public SearchPostGroup(String postGroupId, String postGroupName, String avatarGroup, String bio, Boolean isPublic) {
        super();
        this.postGroupId = postGroupId;
        this.postGroupName = postGroupName;
        this.avatarGroup = avatarGroup;
        this.bio = bio;
        this.isPublic = isPublic;
    }

}