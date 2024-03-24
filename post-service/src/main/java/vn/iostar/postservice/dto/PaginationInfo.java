package vn.iostar.postservice.dto;

import lombok.Data;

@Data
public class PaginationInfo {
	private int page;
	private int pages;
	private long count;
	private int itemsPerPage;
}
