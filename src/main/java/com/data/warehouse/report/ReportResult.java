package com.data.warehouse.report;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportResult {

    List<StatisticResponse> statistics = new ArrayList<>();

    @Getter
    @Setter
    @ToString
    public static class StatisticResponse {

        private String daily;

        private String campaign;

        private String dataSource;

        private String totalClicks;

        private String ctr;

        private String impressions;
    }
}
