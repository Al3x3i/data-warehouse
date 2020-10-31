package com.data.warehouse.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import lombok.Getter;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ReportIntegrationTest implements StaticFixtureTrait {

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
    public void should_upload_csv_to_database() {

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
        resultActions.andDo(print()).andExpect(status().isOk());
    }

    private void givenWarehouseReportRequest() {
        reportRequest = WarehouseReportRequest.builder()
                .startDate(LocalDate.parse("2019-10-13"))
                .endDate(LocalDate.parse("2019-10-13"))
                .build();
    }
}
