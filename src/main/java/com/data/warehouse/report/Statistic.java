package com.data.warehouse.report;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "STATISTIC")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Statistic extends AbstractEntity {

    @Column(name = "DATA_SOURCE", nullable = false)
    private String datasource;

    @Column(name = "CAMPAIGN", nullable = false)
    private String campaign;

    @Column(name = "DAILY", nullable = false)
    private LocalDate daily;

    @Column(name = "CLICKS", nullable = false)
    private Integer clicks;

    @Column(name = "IMPRESSIONS", nullable = false)
    private Integer impressions;

}
