package com.data.warehouse.report;


import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.data.warehouse.report.ReportService.ALLOWED_DIMENSIONS;
import static com.data.warehouse.report.ReportService.ALLOWED_METRICS;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping(value = "/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity report(@RequestBody WarehouseReportRequest request) {

        validateDimensionsAndMetrics(request.getMetrics(), request.getDimensions());

        var result = reportService.handleRequest(request.getMetrics(), request.getDimensions(), request.getStartDate(), request.getEndDate());

        return ResponseEntity.ok(result);
    }

    private void validateDimensionsAndMetrics(Set<String> metrics, Set<String> dimensions) {
        metrics.stream().filter(metric -> !ALLOWED_METRICS.contains(metric)).findFirst().ifPresent(metric -> {
            throw new IllegalArgumentException("Requested invalid metric: " + metric);
        });

        dimensions.stream().filter(dimension -> !ALLOWED_DIMENSIONS.contains(dimension)).findFirst().ifPresent(dimension -> {
            throw new IllegalArgumentException("Requested invalid dimension: " + dimension);
        });

    }

}
