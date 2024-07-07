package vn.iostar.scheduleservice.dto.response;

import lombok.*;
import vn.iostar.scheduleservice.dto.PaginationInfo;

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
