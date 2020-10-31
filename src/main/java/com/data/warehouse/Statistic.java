package com.data.warehouse;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "STATISTIC")
@Builder
@Getter
public class Statistic extends AbstractEntity {

    @Column(name = "DATA_SOURCE", nullable = false)
    private String dataSource;

    @Column(name = "CAMPAIGN", nullable = false)
    private String campaign;

    @Column(name = "DAILY", nullable = false)
    private LocalDate date;

    @Column(name = "CLICKS", nullable = false)
    private Integer clicks;

    @Column(name = "IMPRESSIONS", nullable = false)
    private Integer impressions;

}
