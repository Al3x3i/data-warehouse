package com.data.warehouse.report;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticRepository extends JpaRepository<Statistic, UUID> {


}
