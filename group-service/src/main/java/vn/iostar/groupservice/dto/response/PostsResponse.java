package vn.iostar.groupservice.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

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
	private List<String> comments;
	private List<String> likes;
}
