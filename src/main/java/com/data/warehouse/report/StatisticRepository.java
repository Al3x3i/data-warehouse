package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatisticRepository extends JpaRepository<Statistic, UUID> {

    @Query("SELECT SUM(clicks) " +
            "FROM Statistic " +
            "WHERE DATA_SOURCE = :dataSource AND DAILY BETWEEN :startDate AND :endDate")
    long findSumTotalClicks(String dataSource, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(clicks) " +
            "FROM Statistic " +
            "WHERE DAILY BETWEEN :startDate AND :endDate")
    long findSumTotalClicks(LocalDate startDate, LocalDate endDate);

}
