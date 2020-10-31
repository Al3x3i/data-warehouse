package com.data.warehouse;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticRepository extends JpaRepository<Statistic, UUID> {
}
