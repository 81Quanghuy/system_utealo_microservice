package vn.iostar.postservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponse {
	private Boolean success;
	private String message;
	private Object result;
	private int statusCode;
}
