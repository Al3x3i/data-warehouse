package com.data.warehouse.report;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.data.warehouse.report.ReportService.ALLOWED_DIMENSIONS;
import static com.data.warehouse.report.ReportService.ALLOWED_METRICS;
import static java.util.Objects.isNull;
import static org.hibernate.internal.util.StringHelper.isBlank;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping(value = "/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity report(@RequestBody WarehouseReportRequest request) {

        validateRequest(request);
        var result = reportService.handleRequest(request);

        return ResponseEntity.ok(result);
    }

    private void validateRequest(WarehouseReportRequest request) {

        if (isNull(request.getMetrics())) {
            throw new IllegalArgumentException("Requested metrics cannot be empty");
        } else {
            request.getMetrics().stream().filter(metric -> !ALLOWED_METRICS.contains(metric))
                    .findFirst()
                    .ifPresent(metric -> {
                        throw new IllegalArgumentException("Requested invalid metric: " + metric);
                    });
        }

        if (!isNull(request.getDimensions())) {
            request.getDimensions().stream().filter(dimension -> !ALLOWED_DIMENSIONS.contains(dimension))
                    .findFirst()
                    .ifPresent(dimension -> {
                        throw new IllegalArgumentException("Requested invalid dimension: " + dimension);
                    });
        }

        if (!isNull(request.getDimensionFilters())) {
            request.getDimensionFilters().stream().filter(filter ->
                    !ALLOWED_DIMENSIONS.contains(filter.getDimension()) || isBlank(filter.getFilter()))
                    .findFirst()
                    .ifPresent(filterDimension -> {
                        throw new IllegalArgumentException("Requested invalid filter dimension: " + filterDimension);
                    });
        }
    }

}
