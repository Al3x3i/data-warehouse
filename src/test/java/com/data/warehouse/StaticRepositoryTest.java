package com.data.warehouse;

import com.data.warehouse.report.Statistic;
import com.data.warehouse.report.StatisticRepository;
import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;

@DataJpaTest
@RunWith(SpringRunner.class)
public class StaticRepositoryTest {

    @Autowired
    private StatisticRepository statisticRepository;

    @Test
    public void test_create_statistic_record() {

        // Then
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
}
