package vn.iostar.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Top3UserOfMonth {
	private String userName;
	private String userId;
	private String avatar;
	private Long countPostOfMonth;
	private Long countShareOfMonth;
	private Long countCommentOfMonth;
	private Long total;
}
