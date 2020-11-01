package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseReportRequest {

    private LocalDate startDate;

    private LocalDate endDate;

    private Set<String> dimensions;

    private Set<String> metrics;

    private List<DimensionFilter> dimensionFilters;

    @Data
    @Builder
    public static class DimensionFilter {
        private String dimension;
        private String filter;
    }
}
