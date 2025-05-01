package com.interview.lender.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedResponseDto {
    private List<Object> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
