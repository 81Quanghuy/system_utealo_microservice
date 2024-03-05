package vn.iotstart.groupservice.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_group_requests", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "invitedUserId", "invitingUserId", "postGroupId" }) })
public class PostGroupRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String postGroupRequestId;

//	@ManyToOne
//	@JoinColumn(name = "invitedUserId")
//	private User invitedUser;
//
//	@ManyToOne
//	@JoinColumn(name = "invitingUserId")
//	private User invitingUser;

	@ManyToOne
	@JoinColumn(name = "postGroupId")
	private PostGroup postGroup;
	private Date createDate;

	private Boolean isAccept;
}
