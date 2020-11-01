package com.data.warehouse.csv;

import com.data.warehouse.report.Statistic;
import com.data.warehouse.report.StatisticRepository;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CSVService {

    private static DateTimeFormatter dailyFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");

    @Autowired
    private StatisticRepository statisticRepository;

    public void loadData(InputStream csvInputStream) {

        List<Statistic> statistics = transformCsvToStatistic(csvInputStream);
        statisticRepository.saveAll(statistics);

        log.info("Loaded '{}' records from CSV file", statisticRepository.count());
    }

    private static List<Statistic> transformCsvToStatistic(InputStream csvInputStream) {
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(csvInputStream, "UTF-8"));
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            List<Statistic> statistics = csvParser.getRecords().stream().map(csvRecord -> Statistic.builder()
                    .datasource(csvRecord.get("Datasource"))
                    .campaign(csvRecord.get("Campaign"))
                    .daily(formatDailyDate(csvRecord.get("Daily")))
                    .clicks(Integer.valueOf(csvRecord.get("Clicks")))
                    .impressions(Integer.valueOf(csvRecord.get("Impressions")))
                    .build()).collect(Collectors.toList());

            return statistics;

        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while loading csv data: " + ex.getMessage());
        }
    }

    private static LocalDate formatDailyDate(String date) {
        return LocalDate.parse(date, dailyFormatter);
    }
}
