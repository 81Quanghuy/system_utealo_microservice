package vn.iostar.postservice.dto.response;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.postservice.entity.Comment;

@Data
public class CommentsResponse {
	private String commentId;
	private String content;
	private Date createTime;
	private String photos;
	private String userName;
	private String postId;
	private String shareId;
	private String userAvatar;
	private String userId;
	private List<String> likes;
	private List<String> comments;
	private String userOwner;

	public CommentsResponse(Comment comment, UserProfileResponse user) {
		super();
		this.commentId = comment.getId();
		this.content = comment.getContent();
		this.createTime = comment.getCreateTime();
		this.photos = comment.getPhotos();
		this.userName = user.getUserName();
		if (comment.getPost() != null) {
			this.postId = comment.getPost().getId();
		}
		this.userAvatar = user.getAvatar();
		this.userId = user.getUserId();
		if (comment.getShare() != null) {
			this.shareId = comment.getShare().getId();
		}

	}

}
