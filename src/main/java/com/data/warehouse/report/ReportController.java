package com.data.warehouse.report;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping(value = "/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity report(@RequestBody WarehouseReportRequest request) {

        reportService.getStatistics(request);

        return ResponseEntity.ok().build();
    }
}
