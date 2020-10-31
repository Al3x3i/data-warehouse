package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatisticRepository extends JpaRepository<Statistic, UUID> {

    @Query("SELECT SUM(clicks) " +
            "FROM Statistic " +
            "WHERE DAILY BETWEEN :startDate AND :endDate AND DATA_SOURCE = :dataSource")
    long findSumTotalClicks(String dataSource, LocalDate startDate, LocalDate endDate);

}
