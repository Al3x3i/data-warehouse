package com.data.warehouse.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class ReportService {

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Map<String, String> ALLOWED_REGULAR_DIMENSIONS = Map.of(
            "Campaign", "campaign",
            "Datasource", "dataSource"
    );

    private static final List<String> ALLOWED_METRICS = List.of("Clicks", "CTR", "Impressions");


    public void getStatistics(WarehouseReportRequest request) {

        statisticRepository.findSumTotalClicks(request.startDate, request.endDate);
    }

    public ReportResult handleRequest(Set<String> metrics, Set<String> dimension, LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReportResult> criteriaQuery = builder.createQuery(ReportResult.class);
        Root<Statistic> root = criteriaQuery.from(Statistic.class);

        // Append `Select`, `Group by`
        setRegularDimensionsToQuery(criteriaQuery, root, dimension);

        // Append `Where`
        setTimeDimension(criteriaQuery, builder, root, startDate, endDate);

        List<ReportResult> result = entityManager.createQuery(criteriaQuery).getResultList();

        return null;
    }

    private void setTimeDimension(CriteriaQuery criteriaQuery, CriteriaBuilder builder, Root<Statistic> root,
                                  LocalDate startDate, LocalDate endDate) {
        criteriaQuery.where(builder.between(root.get("date"), startDate, endDate));
    }

    private void setRegularDimensionsToQuery(CriteriaQuery criteriaQuery, Root<Statistic> root, Set<String> dimensions) {
        List<Selection> validMetrics = new ArrayList();
        for (String dimensionName : dimensions) {
            String fieldName = ALLOWED_REGULAR_DIMENSIONS.get(dimensionName);
            if (isNull(fieldName)) {
                log.debug("Invalid requested dimension name: '{}'", dimensionName);
            } else {
                validMetrics.add(root.get(fieldName).alias(dimensionName));
            }
        }
        List<Selection> selectColumns = new ArrayList<>(validMetrics);

        // Append Select columns + aggregations
        criteriaQuery.multiselect(selectColumns);

        // Append Group by columns
        criteriaQuery.groupBy(validMetrics);
    }
}
