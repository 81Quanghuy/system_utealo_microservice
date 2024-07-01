package vn.iostar.userservice.dto;

import java.util.Date;

import lombok.Data;
import vn.iostar.constant.RoleName;
import vn.iostar.userservice.constant.RoleUserGroup;
import vn.iostar.userservice.entity.Account;

@Data
public class UserFileDTO {

	private Account account;
	private String userName;
	private String email;
	private String classUser;
	private String address;
	private Date dateOfBirth;
	private String gender;
	private Double phone;
	private RoleUserGroup roleUserGroup;
	private RoleName roleGroup;
}
