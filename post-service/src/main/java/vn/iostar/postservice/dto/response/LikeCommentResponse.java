package vn.iostar.postservice.dto.response;

import lombok.Data;
import vn.iostar.postservice.entity.Like;

@Data
public class LikeCommentResponse {
	private String likeId;
	private String commentId;
	private String userName;
	
	public LikeCommentResponse(String likeId, String commentId, String userName) {
		super();
		this.likeId = likeId;
		this.commentId = commentId;
		this.userName = userName;
	}
	
	public LikeCommentResponse(Like like, UserProfileResponse user) {
		super();
		this.likeId = like.getId();
		this.commentId = like.getComment().getId();
		this.userName = user.getUserName();
	}
	
	
}
