package vn.iostar.postservice.dto;

import java.util.Date;

import lombok.Data;
import vn.iostar.postservice.constant.PrivacyLevel;

@Data
public class SharePostRequestDTO {
	private String shareId;
	private String content;
	private String postId;
	private String postGroupId;
	private PrivacyLevel privacyLevel;
	private Date createAt;
	private Date updateAt;
	private String userId;
}
