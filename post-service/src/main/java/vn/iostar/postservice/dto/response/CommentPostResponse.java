package vn.iostar.postservice.dto.response;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.postservice.entity.Comment;


@Data
public class CommentPostResponse {
	private String commentId;
	private String content;
	private Date createTime;
	private String photos;
	private String userName;
	private String postId;
	private String userAvatar;
	private String userId;
    private List<Integer> likes;
    private List<Integer> comments;
    private String userOwner;
	
	
	public CommentPostResponse(Comment comment, UserProfileResponse userProfileResponse) {
		super();
		this.commentId = comment.getId();
		this.content = comment.getContent();
		this.createTime = comment.getCreateTime();
		this.photos = comment.getPhotos();
		this.userName = userProfileResponse.getUserName();
		this.postId = comment.getPost().getId();
		this.userAvatar = userProfileResponse.getAvatar();
		this.userId = comment.getUserId();
	}

	public CommentPostResponse(String commentId, String content, Date createTime, String photos, String userName,
			String postId) {
		super();
		this.commentId = commentId;
		this.content = content;
		this.createTime = createTime;
		this.photos = photos;
		this.userName = userName;
		this.postId = postId;
	}

	public CommentPostResponse(String commentId, String content, Date createTime, String photos, String userName,
			String postId, String userAvatar, String userId) {
		super();
		this.commentId = commentId;
		this.content = content;
		this.createTime = createTime;
		this.photos = photos;
		this.userName = userName;
		this.postId = postId;
		this.userAvatar = userAvatar;
		this.userId = userId;
	}
	
}
