package vn.iostar.postservice.dto;

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
public class CountDTO {
	private long countToday;
	private long countInWeek;
	private long countIn1Month;
	private long countIn3Month;
	private long countIn6Month;
	private long countIn9Month;
	private long countIn1Year;
	private double percentNewUser;
	private double percentUserOnline;
	
	public CountDTO(long countToday, long countInWeek, long countIn1Month, long countIn3Month, long countIn6Month,
			long countIn9Month, long countIn1Year) {
		super();
		this.countToday = countToday;
		this.countInWeek = countInWeek;
		this.countIn1Month = countIn1Month;
		this.countIn3Month = countIn3Month;
		this.countIn6Month = countIn6Month;
		this.countIn9Month = countIn9Month;
		this.countIn1Year = countIn1Year;
	}
	
	
}
