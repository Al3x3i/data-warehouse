package com.data.warehouse.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ReporResttIntegrationTest implements StaticFixtureTrait {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Getter
    @Autowired
    private StatisticRepository statisticRepository;

    private WarehouseReportRequest reportRequest;

    private ResultActions resultActions;

    @Test
    @SneakyThrows
    public void should_get_report_with_all_metrics_and_dimensions() {

        // Given
        givenTenFacebookAdsStatistics();
        givenTenGoogleAdsStatistics();
        givenWarehouseReportRequest();

        // When
        resultActions = mockMvc.perform(post("/report")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reportRequest))
        );

        // Then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("statistics[*].daily").isNotEmpty())
                .andExpect(jsonPath("statistics[*].campaign").isNotEmpty())
                .andExpect(jsonPath("statistics[*].dataSource").isNotEmpty())
                .andExpect(jsonPath("statistics[*].totalClicks").isNotEmpty())
                .andExpect(jsonPath("statistics[*].ctr").isNotEmpty())
                .andExpect(jsonPath("statistics[*].impressions").isNotEmpty());
    }

    @Test
    @SneakyThrows
    public void should_get_report_with_one_dimension_and_metric() {

        // Given
        givenTenFacebookAdsStatistics();
        givenTenGoogleAdsStatistics();
        givenWarehouseReportRequestWithOneMetricAndDimension();

        // When
        resultActions = mockMvc.perform(post("/report")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reportRequest))
        );

        // Then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("statistics[*].daily").doesNotExist())
                .andExpect(jsonPath("statistics[*].campaign").doesNotExist())
                .andExpect(jsonPath("statistics[*].dataSource").isNotEmpty())
                .andExpect(jsonPath("statistics[*].totalClicks").isNotEmpty())
                .andExpect(jsonPath("statistics[*].ctr").doesNotExist())
                .andExpect(jsonPath("statistics[*].impressions").doesNotExist());
    }

    @Test
    @SneakyThrows
    public void should_fail_to_get_report_for_invalid_dimension() {

        // Given
        givenInvalidWarehouseReportRequest();

        // When
        resultActions = mockMvc.perform(post("/report")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reportRequest))
        );

        // Then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    private void givenWarehouseReportRequest() {
        reportRequest = WarehouseReportRequest.builder()
                .startDate(LocalDate.parse("2019-10-13"))
                .endDate(LocalDate.parse("2019-10-13"))
                .dimensions(Set.of("datasource", "campaign", "daily"))
                .metrics(Set.of("clicks", "ctr", "impressions"))
                .build();
    }

    private void givenWarehouseReportRequestWithOneMetricAndDimension() {
        reportRequest = WarehouseReportRequest.builder()
                .startDate(LocalDate.parse("2019-10-13"))
                .endDate(LocalDate.parse("2019-10-13"))
                .dimensions(Set.of("datasource"))
                .metrics(Set.of("clicks"))
                .build();
    }

    private void givenInvalidWarehouseReportRequest() {
        reportRequest = WarehouseReportRequest.builder()
                .startDate(LocalDate.parse("2019-10-13"))
                .endDate(LocalDate.parse("2019-10-13"))
                .dimensions(Set.of("test"))
                .metrics(Set.of("test"))
                .build();
    }
}
