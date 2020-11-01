package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseReportRequest {

    LocalDate startDate;

    LocalDate endDate;

    Set<String> dimensions;

    Set<String> metrics;
}
