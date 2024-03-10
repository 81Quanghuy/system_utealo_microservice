package vn.iostar.postservice.dto.response;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.constant.RoleName;
import vn.iostar.postservice.entity.Post;

@Data
public class PostsResponse {
	private String postId;
	private Date postTime;
	private Date updateAt;
	private String content;
	private String photos;
	private String files;
	private String location;
	private String userId;
	private String userName;
	private String avatarUser;
	private String postGroupId;
	private String postGroupName;
	private String avatarGroup;
	private String groupType;
	private List<Integer> comments;
	private List<Integer> likes;
	private RoleName roleName;
	private PrivacyLevel privacyLevel;

	public PostsResponse(Post post) {
		this.postId = post.getId();
		this.postTime = post.getPostTime();
		this.updateAt = post.getUpdatedAt();
		this.content = post.getContent();
		this.photos = post.getPhotos();
		this.files = post.getFiles();
		this.location = post.getLocation();
		this.userId = post.getUserId();
//		this.userName = post.getUserName();
//		this.avatarUser = post.getAvatarUser();
//		this.postGroupId = post.getPostGroupId();
//		this.postGroupName = post.getPostGroupName();
//		this.avatarGroup = post.getAvatarGroup();
//		this.groupType = post.getGroupType();
//		this.comments = post.getComments();
//		this.likes = post.getLikes();
//		this.roleName = post.getRoleName();
		this.privacyLevel = post.getPrivacyLevel();
	}
}
