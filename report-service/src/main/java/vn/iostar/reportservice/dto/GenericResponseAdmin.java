package vn.iostar.reportservice.dto;

import lombok.*;

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
