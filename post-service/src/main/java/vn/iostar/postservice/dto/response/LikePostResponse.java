package vn.iostar.dto;



import lombok.Data;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Like;

@Data
public class LikePostResponse {
	
	private String likeId;
	private String postId;
	private String userName;
	
	public LikePostResponse(Like like, UserProfileResponse userProfileResponse) {
		super();
		this.likeId = like.getId();
		this.postId = like.getPost().getId();
		this.userName = userProfileResponse.getUserName();
	}

	public LikePostResponse(String likeId, String postId, String userName) {
		super();
		this.likeId = likeId;
		this.postId = postId;
		this.userName = userName;
	}

	
	
	
}
