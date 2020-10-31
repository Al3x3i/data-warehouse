package com.data.warehouse.report;

import java.time.LocalDate;
import lombok.Getter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;

@DataJpaTest
@RunWith(SpringRunner.class)
public class StaticRepositoryTest implements StaticFixtureTrait {

    @Autowired
    @Getter
    private StatisticRepository statisticRepository;

    @Test
    public void should_create_statistic_record() {

        // Given
        var statistic = Statistic.builder()
                .dataSource("Google Ads")
                .campaign("Adventmarkt Touristik")
                .date(LocalDate.now())
                .clicks(7)
                .impressions(22425)
                .build();

        // When
        statisticRepository.save(statistic);

        // Then
        then(statisticRepository.count()).isNotZero();
    }

    @Test
    public void should_get_total_clicks_by_data_source_date_range() {

        // Given
        givenTenGoogleAdsStatistics();
        givenTenFacebookAdsStatistics();

        // When
        long totalClicks = statisticRepository.findSumTotalClicks("Google Ads", LocalDate.parse("2019-10-13"), LocalDate.parse("2019-10-13"));

        // Then
        then(totalClicks).isEqualTo(100);
    }
}
