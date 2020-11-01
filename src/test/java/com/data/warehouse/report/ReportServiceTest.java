package com.data.warehouse.report;

import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ReportServiceTest implements StaticFixtureTrait {

    @Autowired
    @Getter
    private StatisticRepository statisticRepository;

    @Autowired
    @Getter
    private ReportService reportService;


    @Before
    public void setup() {
        statisticRepository.deleteAll();
    }

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

        var request = WarehouseReportRequest.builder()
                .dimensions(Set.of("datasource", "campaign", "daily"))
                .metrics(Set.of("clicks", "ctr", "impressions"))
                .startDate(LocalDate.parse("2010-10-13"))
                .endDate(LocalDate.parse("2019-10-13")).build();

        // When
        ReportResult reportResult = reportService.handleRequest(request);

        // Then
        then(reportResult.getStatistics().get(0).getDaily()).isNotNull();
        then(reportResult.getStatistics().get(0).getCampaign()).isNotNull();
        then(reportResult.getStatistics().get(0).getDataSource()).isNotNull();
        then(reportResult.getStatistics().get(0).getCtr()).startsWith("33.33");
        then(reportResult.getStatistics().get(0).getClicks()).isEqualTo("100");
        then(reportResult.getStatistics().get(0).getImpressions()).isEqualTo("300");
    }

    @Test
    public void should_get_report_result_for_all_metrics() {

        // Given
        givenTenGoogleAdsStatistics();
        givenTenFacebookAdsStatistics();
        givenTenTwitterAdsStatistics();

        var request = WarehouseReportRequest.builder()
                .dimensions(Set.of())
                .metrics(Set.of("clicks", "ctr", "impressions"))
                .startDate(LocalDate.parse("2010-10-13"))
                .endDate(LocalDate.parse("2019-10-13")).build();

        // When
        ReportResult reportResult = reportService.handleRequest(request);

        // Then
        then(reportResult.getStatistics().size()).isEqualTo(1);
        then(reportResult.getStatistics().get(0).getDaily()).isNull();
        then(reportResult.getStatistics().get(0).getCampaign()).isNull();
        then(reportResult.getStatistics().get(0).getDataSource()).isNull();
        then(reportResult.getStatistics().get(0).getCtr()).startsWith("33.33");
        then(reportResult.getStatistics().get(0).getClicks()).isEqualTo("300");
        then(reportResult.getStatistics().get(0).getImpressions()).isEqualTo("900");
    }

    @Test
    public void should_get_report_result_for_impressions_metrics_and_daily_domain() {

        // Given
        givenTenGoogleAdsStatistics("2019-10-12");
        givenTenGoogleAdsStatistics("2010-10-13");

        var request = WarehouseReportRequest.builder()
                .dimensions(Set.of("daily"))
                .metrics(Set.of("clicks", "ctr", "impressions"))
                .startDate(LocalDate.parse("2010-10-13"))
                .endDate(LocalDate.parse("2019-10-13")).build();

        // When
        ReportResult reportResult = reportService.handleRequest(request);

        // Then
        then(reportResult.getStatistics().size()).isEqualTo(2);
        then(reportResult.getStatistics().get(0).getDaily()).isNotBlank();
        then(reportResult.getStatistics().get(0).getCampaign()).isNull();
        then(reportResult.getStatistics().get(0).getDataSource()).isNull();
        then(reportResult.getStatistics().get(0).getCtr()).isEqualTo("50.0");
        then(reportResult.getStatistics().get(0).getClicks()).isEqualTo("100");
        then(reportResult.getStatistics().get(0).getImpressions()).isEqualTo("200");
    }

    @Test
    public void should_get_report_result_for_ctr_metrics() {

        // Given
        givenTenGoogleAdsStatistics();
        givenTenFacebookAdsStatistics();
        givenTenTwitterAdsStatistics();

        var request = WarehouseReportRequest.builder()
                .dimensions(Set.of())
                .metrics(Set.of("clicks"))
                .startDate(LocalDate.parse("2010-10-13"))
                .endDate(LocalDate.parse("2019-10-13")).build();

        // When
        ReportResult reportResult = reportService.handleRequest(request);

        // Then
        then(reportResult.getStatistics().size()).isEqualTo(1);
        then(reportResult.getStatistics().get(0).getDaily()).isNull();
        then(reportResult.getStatistics().get(0).getCampaign()).isNull();
        then(reportResult.getStatistics().get(0).getDataSource()).isNull();
        then(reportResult.getStatistics().get(0).getCtr()).isNull();
        then(reportResult.getStatistics().get(0).getClicks()).isEqualTo("300");
        then(reportResult.getStatistics().get(0).getImpressions()).isNull();
    }
}
