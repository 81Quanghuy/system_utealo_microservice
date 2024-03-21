package vn.iostar.postservice.dto.response;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.postservice.entity.Share;

@Data
public class ShareResponse {
	private String shareId;
	private String content;
	private Date createAt;
	private Date updateAt;
	private String postId;
	private String userId;
	private List<String> comments;
	private List<String> likes;

	public ShareResponse(Share share, UserProfileResponse user) {
		this.shareId = share.getId();
		this.content = share.getContent();
		this.createAt = share.getCreateAt();
		this.updateAt = share.getUpdateAt();
		this.postId = share.getPost().getId();
		this.userId = user.getUserId();
		this.comments = share.getComments();
		this.likes = share.getLikes();
	}

}
