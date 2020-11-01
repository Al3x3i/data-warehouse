package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseReportRequest {

    private LocalDate startDate;

    private LocalDate endDate;

    private LinkedHashSet<String> dimensions;

    private LinkedHashSet<String> metrics;

    private List<DimensionFilter> dimensionFilters;

    @Data
    @Builder
    public static class DimensionFilter {
        private String dimension;
        private String filter;
    }
}
