package vn.iostar.postservice.dto.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Builder;
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
	private String video;
	private String location;
	private String userId;
	private String userName;
	private String avatarUser;
	private String postGroupId;
	private String postGroupName;
	private String avatarGroup;
	private String groupType;
	private List<String> comments;
	private List<String> likes;
	private RoleName roleName;
	private PrivacyLevel privacyLevel;

	public PostsResponse(Post post, UserProfileResponse user, GroupProfileResponse group) {
		this.postId = post.getId();
		this.postTime = post.getPostTime();
		this.updateAt = post.getUpdatedAt();
		this.content = post.getContent();
		this.photos = post.getPhotos();
		this.files = post.getFiles();
		this.video = post.getVideo();
		this.location = post.getLocation();
		this.userId = post.getUserId();
		if (user != null) {
			this.userName = user.getUserName();
			this.avatarUser = user.getAvatar();
            this.roleName = user.getRoleName();
		}
		if (group!= null && post.getGroupId()!= null &&  post.getGroupId().equals(group.getId())) {
			this.postGroupId = group.getId();
			this.postGroupName = group.getGroupName();
			this.avatarGroup = group.getGroupAvatar();
			this.groupType = group.getGroupType();

		}
		this.privacyLevel = post.getPrivacyLevel();
		if(post.getComments() != null) {
			this.comments = post.getComments();
		} else {
			this.comments = new ArrayList<>();
		}
		if(post.getLikes() != null) {
			this.likes = post.getLikes();
		} else {
			this.likes = new ArrayList<>();
		}
	}
	public PostsResponse(Post post, String content ,UserProfileResponse user, GroupProfileResponse group) {
		this.postId = post.getId();
		this.postTime = post.getPostTime();
		this.updateAt = post.getUpdatedAt();
		this.content =content;
		if(content== null){
			this.photos = post.getPhotos();
			this.files = post.getFiles();
			this.video = post.getVideo();
		} else{
			this.photos = null;
			this.files = null;
			this.video = null;
		}
		this.location = post.getLocation();
		this.userId = post.getUserId();
		if (user != null) {
			this.userName = user.getUserName();
			this.avatarUser = user.getAvatar();
			this.roleName = user.getRoleName();
		}
		if (group!= null && post.getGroupId()!= null &&  post.getGroupId().equals(group.getId())) {
			this.postGroupId = group.getId();
			this.postGroupName = group.getGroupName();
			this.avatarGroup = group.getGroupAvatar();
			this.groupType = group.getGroupType();
			if ("private".equalsIgnoreCase(group.getGroupType())) {
				this.content = "Bài viết bị ẩn vì lấy từ nhóm riêng tư";
			}
		}
		this.privacyLevel = post.getPrivacyLevel();
		if(post.getComments() != null) {
			this.comments = post.getComments();
		} else {
			this.comments = new ArrayList<>();
		}
		if(post.getLikes() != null) {
			this.likes = post.getLikes();
		} else {
			this.likes = new ArrayList<>();
		}
	}
}
