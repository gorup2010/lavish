package com.nashrookie.lavish.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PaginationResponse<T> {
    @Schema(description = "Current page")
    private Integer page;
    @Schema(description = "Total elements")
    private Integer total;
    @Schema(description = "Total pages")
    private Integer totalPages;
    private List<T> data;
}
