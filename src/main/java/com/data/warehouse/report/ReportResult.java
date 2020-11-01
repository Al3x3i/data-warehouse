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

        @JsonInclude(Include.NON_NULL)
        private String daily;

        @JsonInclude(Include.NON_NULL)
        private String campaign;

        @JsonInclude(Include.NON_NULL)
        private String dataSource;

        @JsonInclude(Include.NON_NULL)
        private String totalClicks;

        @JsonInclude(Include.NON_NULL)
        private String ctr;

        @JsonInclude(Include.NON_NULL)
        private String impressions;
    }
}
