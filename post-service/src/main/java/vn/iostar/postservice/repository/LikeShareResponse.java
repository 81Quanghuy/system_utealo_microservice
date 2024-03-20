package vn.iostar.postservice.repository;



import lombok.Data;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Like;

@Data
public class LikeShareResponse {
	
	private String likeId;
	private String shareId;
	private String userName;
	
	public LikeShareResponse(Like like, UserProfileResponse user) {
		super();
		this.likeId = like.getId();
		this.shareId = like.getShare().getId();
		this.userName = user.getUserName();
	}

	public LikeShareResponse(String likeId, String shareId, String userName) {
		super();
		this.likeId = likeId;
		this.shareId = shareId;
		this.userName = userName;
	}

	
	
	
}
