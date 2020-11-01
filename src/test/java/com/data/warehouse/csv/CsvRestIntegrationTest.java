package com.data.warehouse.csv;

import com.data.warehouse.report.StatisticRepository;
import java.io.FileInputStream;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class CsvRestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StatisticRepository statisticRepository;

    private ResultActions resultActions;

    private static final String CSV_PATH = "src/test/resources/documentation.csv";

    @Test
    @SneakyThrows
    public void should_upload_csv_to_database() {

        // Given
        FileInputStream fis = new FileInputStream(CSV_PATH);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fis);

        // When
        resultActions = mockMvc
                .perform(post("/csv/upload")
                        .contentType("text/csv")
                        .content(multipartFile.getBytes())
                );

        // Then
        resultActions.andExpect(status().isOk());
        then(statisticRepository.count()).isNotZero();

        resultActions.andDo(
                document("create-todo-item"


                )
        );
    }
}