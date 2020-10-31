package com.data.warehouse;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class StaticRepositoryTest {

    @Autowired
    private StatisticRepository statisticRepository;

    @Test
    public void test(){

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
