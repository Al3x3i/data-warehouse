package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public interface StaticFixtureTrait {

    StatisticRepository getStatisticRepository();

    default void givenTenGoogleAdsStatistics() {
        givenTenGoogleAdsStatistics("2019-10-13");
    }

    default void givenTenGoogleAdsStatistics(String date) {
        var statistics = IntStream.range(0, 10).mapToObj(index ->
                Statistic.builder()
                        .datasource("Google Ads")
                        .campaign("Google Touristik")
                        .daily(LocalDate.parse(date))
                        .clicks(10)
                        .impressions(20)
                        .build()).collect(toList());
        getStatisticRepository().saveAll(statistics);
    }

    default void givenTenFacebookAdsStatistics() {
        var statistics = IntStream.range(0, 10).mapToObj(index ->
                Statistic.builder()
                        .datasource("Facebook Ads")
                        .campaign("Facebook Touristik")
                        .daily(LocalDate.parse("2019-10-13"))
                        .clicks(10)
                        .impressions(30)
                        .build()).collect(toList());
        getStatisticRepository().saveAll(statistics);
    }

    default void givenTenTwitterAdsStatistics() {
        var statistics = IntStream.range(0, 10).mapToObj(index ->
                Statistic.builder()
                        .datasource("Twitter Ads")
                        .campaign("Twitter Touristik")
                        .daily(LocalDate.parse("2019-10-13"))
                        .clicks(10)
                        .impressions(40)
                        .build()).collect(toList());
        getStatisticRepository().saveAll(statistics);
    }
}
