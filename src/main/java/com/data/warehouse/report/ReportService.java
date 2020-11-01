package com.data.warehouse.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReportService {

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private static final List<String> ALLOWED_METRICS = List.of("clicks", "ctr", "impressions");

    private static final List<String> ALLOWED_DIMENSIONS = List.of("campaign", "datasource", "daily");


    public void getStatistics(WarehouseReportRequest request) {

        statisticRepository.findSumTotalClicks(request.startDate, request.endDate);
    }

    public ReportResult handleRequest(Set<String> metrics, Set<String> dimension, LocalDate startDate, LocalDate endDate) {

        // Builder
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = builder.createTupleQuery();
        Root<Statistic> root = criteriaQuery.from(Statistic.class);

        // Append `Select`, `Group by`
        setSelectStatement(criteriaQuery, builder, root, metrics, dimension);

        // Append `Where`
        setWhereStatement(criteriaQuery, builder, root, startDate, endDate);

        // Request
        List<Tuple> result = entityManager.createQuery(criteriaQuery).getResultList();

        // TODO parse result

        return null;
    }

    private void setWhereStatement(CriteriaQuery criteriaQuery, CriteriaBuilder builder, Root<Statistic> root,
                                   LocalDate startDate, LocalDate endDate) {
        criteriaQuery.where(builder.between(root.get("daily"), startDate, endDate));
    }

    private void setSelectStatement(CriteriaQuery criteriaQuery, CriteriaBuilder builder, Root<Statistic> root,
                                    Set<String> metrics, Set<String> dimensions) {
        List<Selection> validSelections = new ArrayList();

        for (String dimensionName : dimensions) {
            if (ALLOWED_DIMENSIONS.contains(dimensionName)) {
                validSelections.add(root.get(dimensionName).alias(dimensionName));
            } else {
                log.debug("Invalid requested dimension name: '{}'", dimensionName);
            }
        }

        List<Selection> selections = new ArrayList<>(validSelections);

        setSelectMetricAggregation(metrics, selections, builder, root);

        // Append Select columns + aggregations
        criteriaQuery.multiselect(selections);
        // Append Group by columns
        criteriaQuery.groupBy(validSelections);
    }

    private void setSelectMetricAggregation(Set<String> metrics, List<Selection> selections, CriteriaBuilder builder, Root<Statistic> root) {
        for (String metric : metrics) {
            if (metric.equals("clicks")) {
                Selection totalClicks = builder.sum(root.get("clicks"));
                selections.add(totalClicks);
            }
        }
    }
}
