package vn.iostar.groupservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponseAdmin {
	private Boolean success;
	private String message;
	private Object result;
	private int statusCode;
	private PaginationInfo pagination;
}
