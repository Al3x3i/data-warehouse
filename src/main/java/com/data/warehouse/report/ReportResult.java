package com.data.warehouse.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResult {

    List<StatisticResponse> statistics = new ArrayList<>();

    @Getter
    @Setter
    public static class StatisticResponse {

        private String daily;

        private String campaign;

        private String dataSource;

        private String totalClicks;

        private String ctr;

        private String impressions;
    }
}
