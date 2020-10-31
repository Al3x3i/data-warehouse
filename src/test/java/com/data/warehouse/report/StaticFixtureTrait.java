package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public interface StaticFixtureTrait {

    StatisticRepository getStatisticRepository();

    default void givenTenGoogleAdsStatistics() {
        var statistics = IntStream.range(0, 10).mapToObj(index ->
                Statistic.builder()
                        .dataSource("Google Ads")
                        .campaign("Adventmarkt Touristik")
                        .date(LocalDate.parse("2019-10-13"))
                        .clicks(10)
                        .impressions(22425)
                        .build()).collect(toList());
        getStatisticRepository().saveAll(statistics);
    }

    default void givenTenFacebookAdsStatistics() {
        var statistics = IntStream.range(0, 10).mapToObj(index ->
                Statistic.builder()
                        .dataSource("Facebook Ads")
                        .campaign("Adventmarkt Touristik")
                        .date(LocalDate.parse("2019-10-13"))
                        .clicks(10)
                        .impressions(22425)
                        .build()).collect(toList());
        getStatisticRepository().saveAll(statistics);
    }
}
