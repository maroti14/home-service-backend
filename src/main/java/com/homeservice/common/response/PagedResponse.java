package com.homeservice.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {

	private List<T> content;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean isLast;
	private boolean isFirst;

	public static <T> PagedResponse<T> of(Page<T> page) {
		return PagedResponse.<T>builder().content(page.getContent()).pageNumber(page.getNumber())
				.pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
				.isLast(page.isLast()).isFirst(page.isFirst()).build();
	}
}