package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.LinkedHashSet;

import static java.util.Arrays.stream;

public interface StaticFixtureTrait {

    StatisticRepository getStatisticRepository();


    default LinkedHashSet getLinkedHashSet(String... values) {
        var linkedHashSet = new LinkedHashSet<>();
        stream(values).forEach(linkedHashSet::add);
        return linkedHashSet;
    }

    default void givenTenGoogleAdsStatistics() {
        givenTenGoogleAdsStatistics("2019-10-13");
    }

    default void givenTenGoogleAdsStatistics(String date) {
        var statistic = Statistic.builder()
                .datasource("Google Ads")
                .campaign("Google Touristik")
                .daily(LocalDate.parse(date))
                .clicks(100)
                .impressions(200)
                .build();
        getStatisticRepository().save(statistic);
    }

    default void givenTenFacebookAdsStatistics() {
        var statistic = Statistic.builder()
                .datasource("Facebook Ads")
                .campaign("Facebook Touristik")
                .daily(LocalDate.parse("2019-10-13"))
                .clicks(100)
                .impressions(300)
                .build();
        getStatisticRepository().save(statistic);
    }

    default void givenTenTwitterAdsStatistics() {
        var statistic = Statistic.builder()
                .datasource("Twitter Ads")
                .campaign("Twitter Touristik")
                .daily(LocalDate.parse("2019-10-13"))
                .clicks(100)
                .impressions(400)
                .build();
        getStatisticRepository().save(statistic);
    }
}
