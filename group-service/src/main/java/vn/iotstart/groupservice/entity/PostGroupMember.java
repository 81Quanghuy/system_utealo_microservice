package vn.iotstart.groupservice.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iotstart.groupservice.constant.RoleUserGroup;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "postgroupmember")
public class PostGroupMember implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int postGroupMemberId;

	@Column(name = "user_id")
	private String userId;

	@ManyToMany(mappedBy = "postGroupMembers")
	@Cascade(value = { CascadeType.REMOVE })
	private List<PostGroup> postGroup = new ArrayList<>();

//	@ManyToOne(cascade = jakarta.persistence.CascadeType.ALL)
//	@JoinColumn(name = "userId")
//	private User user;

	@Enumerated(EnumType.STRING)
	private RoleUserGroup roleUserGroup;

}
