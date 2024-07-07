package vn.iostar.reportservice.dto;

import lombok.Data;
import vn.iostar.constant.RoleName;
import vn.iostar.reportservice.constant.PrivacyLevel;
import vn.iostar.reportservice.entity.Report;

import java.util.Date;

@Data
public class ReportsResponse {
	
	private String reportId;
	private Date postTime;
	private String content;
	private String photos;
	private String files;
	private String userName;
	private String avatarUser;
	private RoleName roleName;
	private PrivacyLevel privacyLevel;
	
	public ReportsResponse(Report report, UserProfileResponse user) {
		this.privacyLevel = report.getPrivacyLevel();
		this.reportId = report.getId();
		this.content = report.getContent();
		this.files = report.getFiles();
		this.photos = report.getPhotos();
		this.userName = user.getUserName();
		this.avatarUser = user.getAvatar();
		this.roleName = user.getRoleName();
	}
}
