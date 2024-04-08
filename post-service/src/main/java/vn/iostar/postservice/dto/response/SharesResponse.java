package vn.iostar.postservice.dto.response;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.constant.RoleName;
import vn.iostar.postservice.entity.Share;

@Data
public class SharesResponse {

    private String shareId;
    private String content;
    private Date createAt;
    private Date updateAt;
    private RoleName roleName;
    private String userId;
    private String userName;
    private String avatarUser;
    private PrivacyLevel privacyLevel;
    private PostsResponse postsResponse;
    private String postGroupId;
    private String postGroupName;
    private List<String> comments;
    private List<String> likes;

    public SharesResponse(Share share, UserProfileResponse user, GroupProfileResponse group) {
        this.shareId = share.getId();
        this.content = share.getContent();
        this.createAt = share.getCreateAt();
        this.updateAt = share.getUpdateAt();
        this.postsResponse = new PostsResponse(share.getPost(), user, group);
        this.userId = share.getUserId();
        this.avatarUser =user.getAvatar();
        this.roleName = user.getRoleName();
        this.userName = user.getUserName();
        this.privacyLevel = share.getPrivacyLevel();
        if (share.getPostGroupId() != null) {
            this.postGroupId = group.getId();
            this.postGroupName = group.getGroupName();
        }
        if (share.getComments() != null) {
            this.comments = share.getComments();
        } else {
            this.comments = null;
        }
        if (share.getLikes() != null) {
            this.likes = share.getLikes();
        } else {
            this.likes = null;
        }
    }

//    public SharesResponse(Share share) {
//        this.shareId = share.getId();
//        this.content = share.getContent();
//        this.createAt = share.getCreateAt();
//        this.updateAt = share.getUpdateAt();
//        this.postsResponse = new PostsResponse(share.getPost());
//        this.userId = share.getUser().getUserId();
//        this.avatarUser = share.getUser().getProfile().getAvatar();
//        this.roleName = share.getUser().getRole().getRoleName();
//        this.userName = share.getUser().getUserName();
//        this.privacyLevel = share.getPrivacyLevel();
//        if (share.getPostGroup() != null) {
//            this.postGroupId = share.getPostGroup().getPostGroupId();
//            this.postGroupName = share.getPostGroup().getPostGroupName();
//        }
//
//    }
}
