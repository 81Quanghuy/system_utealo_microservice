package vn.iotstart.groupservice.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "postgroup")
public class PostGroup implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int postGroupId;

	@Column(unique = true, columnDefinition = "nvarchar(255)")
	private String postGroupName;

	private String avatarGroup;
	private String backgroundGroup;

	@Column(columnDefinition = "nvarchar(255)")
	private String bio;

	private Boolean isPublic = true; // true: private, false: public
	private Boolean isApprovalRequired = false; // Yêu cầu phê duyệt

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "postGroup_postGroupMember", joinColumns = @JoinColumn(name = "postGroupId"), inverseJoinColumns = @JoinColumn(name = "postGroupMemberId"))
	private Set<PostGroupMember> postGroupMembers = new HashSet<>();

//	@OneToMany(mappedBy = "postGroup", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	private List<Post> posts;
//
//	@OneToMany(mappedBy = "postGroup", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	private List<Share> shares;

	@OneToMany(mappedBy = "postGroup", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<PostGroupRequest> postGroupRequests;

//	@OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	private List<Message> messages;
//
//	@OneToMany(mappedBy = "postGroup", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//	private List<Notification> notifications;

	private Date createDate =new Date();
	private Date updateDate = new Date();
}
