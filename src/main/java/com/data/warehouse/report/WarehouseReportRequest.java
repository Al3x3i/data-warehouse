package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseReportRequest {

    LocalDate startDate;

    LocalDate endDate;

    List<String> dimensions;

    List<String> metrics;
}
