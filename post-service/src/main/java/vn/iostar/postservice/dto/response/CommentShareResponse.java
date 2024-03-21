package vn.iostar.postservice.dto.response;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.postservice.entity.Comment;


@Data
public class CommentShareResponse {
	private String commentId;
	private String content;
	private Date createTime;
	private String photos;
	private String userName;
	private String shareId;
	private String userAvatar;
	private String userId;
    private List<Integer> likes;
    private List<Integer> comments;
    private String userOwner;
	
	public CommentShareResponse(Comment comment, UserProfileResponse userProfileResponse) {
		super();
		this.commentId = comment.getId();
		this.content = comment.getContent();
		this.createTime = comment.getCreateTime();
		this.photos = comment.getPhotos();
		this.userName = userProfileResponse.getUserName();
		this.shareId = comment.getShare().getId();
		this.userAvatar = userProfileResponse.getAvatar();
		this.userId = userProfileResponse.getUserId();
	}

	public CommentShareResponse(String commentId, String content, Date createTime, String photos, String userName,
								String shareId) {
		super();
		this.commentId = commentId;
		this.content = content;
		this.createTime = createTime;
		this.photos = photos;
		this.userName = userName;
		this.shareId = shareId;
	}

	public CommentShareResponse(String commentId, String content, Date createTime, String photos, String userName,
								String shareId, String userAvatar, String userId) {
		super();
		this.commentId = commentId;
		this.content = content;
		this.createTime = createTime;
		this.photos = photos;
		this.userName = userName;
		this.shareId = shareId;
		this.userAvatar = userAvatar;
		this.userId = userId;
	}
	
}
