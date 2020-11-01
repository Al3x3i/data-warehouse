package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringRunner.class)
@SpringBootApplication
public class ReportServiceTest implements StaticFixtureTrait {

    @Autowired
    @Getter
    private StatisticRepository statisticRepository;

    @Autowired
    @Getter
    private ReportService reportService;


    @Test
    public void should_create_statistic_record() {

        // Given
        var statistic = Statistic.builder()
                .datasource("Google Ads")
                .campaign("Adventmarkt Touristik")
                .daily(LocalDate.now())
                .clicks(7)
                .impressions(22425)
                .build();

        // When
        statisticRepository.save(statistic);

        // Then
        then(statisticRepository.count()).isNotZero();
    }

    @Test
    public void should_get_report_result_for_all_dimensions_and_all_metrics() {

        // Given
        givenTenGoogleAdsStatistics();
        givenTenFacebookAdsStatistics();
        givenTenTwitterAdsStatistics();

        Set<String> dimension = Set.of("datasource", "campaign", "daily");
        Set<String> metrics = Set.of("clicks", "ctr", "impressions");

        // When
        ReportResult reportResult = reportService.handleRequest(metrics, dimension, LocalDate.parse("2019-10-13"), LocalDate.parse("2019-10-13"));

        // Then
        then(reportResult.getStatistics().get(0).getDaily()).isNotNull();
        then(reportResult.getStatistics().get(0).getCampaign()).isNotNull();
        then(reportResult.getStatistics().get(0).getDataSource()).isNotNull();
        then(reportResult.getStatistics().get(0).getCtr()).isNotNull();
        then(reportResult.getStatistics().get(0).getTotalClicks()).isNotNull();
        then(reportResult.getStatistics().get(0).getImpressions()).isNotNull();
    }

    @Test
    public void should_get_report_result_for_all_metrics() {

        // Given
        givenTenGoogleAdsStatistics();
        givenTenFacebookAdsStatistics();
        givenTenTwitterAdsStatistics();

        Set<String> dimension = Set.of();
        Set<String> metrics = Set.of("clicks", "ctr", "impressions");

        // When
        ReportResult reportResult = reportService.handleRequest(metrics, dimension, LocalDate.parse("2019-10-13"), LocalDate.parse("2019-10-13"));

        // Then
        then(reportResult.getStatistics().size()).isEqualTo(1);
        then(reportResult.getStatistics().get(0).getDaily()).isNull();
        then(reportResult.getStatistics().get(0).getCampaign()).isNull();
        then(reportResult.getStatistics().get(0).getDataSource()).isNull();
        then(reportResult.getStatistics().get(0).getCtr()).isNotNull();
        then(reportResult.getStatistics().get(0).getTotalClicks()).isNotNull();
        then(reportResult.getStatistics().get(0).getImpressions()).isNotNull();
    }
}
