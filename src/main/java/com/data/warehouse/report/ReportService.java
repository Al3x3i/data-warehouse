package com.data.warehouse.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    private StatisticRepository statisticRepository;

    public void getStatistics(WarehouseReportRequest request) {

        statisticRepository.findSumTotalClicks(request.startDate, request.endDate);
    }

}
