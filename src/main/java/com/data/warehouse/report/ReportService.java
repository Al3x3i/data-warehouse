package com.data.warehouse.report;

import com.data.warehouse.report.ReportResult.StatisticResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;

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

        // TODO
//        statisticRepository.findSumTotalClicks(request.startDate, request.endDate);
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

        // Append 'Order`
        setOrderStatement(builder, criteriaQuery);

        // Request
        List<Tuple> result = entityManager.createQuery(criteriaQuery).getResultList();

        // Mapping
        ReportResult reportResult = mapDatabaseResponseToReportResult(criteriaQuery, result);
        return reportResult;
    }

    private void setOrderStatement(CriteriaBuilder builder, CriteriaQuery<Tuple> criteriaQuery) {
        int maxColumns = criteriaQuery.getSelection().getCompoundSelectionItems().size();
        List<Order> orders = new ArrayList<>();
        // Only first two columns are ordered
        for (int index = 0; index < 2 && index < maxColumns; index++) {
            Expression expression = (Expression) criteriaQuery.getSelection().getCompoundSelectionItems().get(index);
            orders.add(builder.asc(expression));
        }
        criteriaQuery.orderBy(orders);
    }

    private ReportResult mapDatabaseResponseToReportResult(CriteriaQuery<Tuple> criteriaQuery, List<Tuple> result) {

        List<String> selectedColumns = criteriaQuery.getSelection().getCompoundSelectionItems().stream().map(TupleElement::getAlias).collect(toList());

        ReportResult reportResult = new ReportResult();

        for (Tuple tuple : result) {

            StatisticResponse statisticResponse = new StatisticResponse();
            selectedColumns.forEach(column -> {

                switch (column) {
                    case "datasource":
                        statisticResponse.setDataSource(tuple.get(column).toString());
                        break;
                    case "campaign":
                        statisticResponse.setCampaign(tuple.get(column).toString());
                        break;
                    case "totalClicks":
                        statisticResponse.setTotalClicks(tuple.get(column).toString());
                        break;
                    case "ctr":
                        statisticResponse.setCtr(tuple.get(column).toString());
                        break;
                    case "daily":
                        statisticResponse.setDaily(tuple.get(column).toString());
                        break;
                    case "impressions":
                        statisticResponse.setImpressions(tuple.get(column).toString());
                        break;
                }
            });
            reportResult.getStatistics().add(statisticResponse);
        }
        return reportResult;

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
                Selection totalClicks = builder.sum(root.get("clicks")).as(Integer.class).alias("totalClicks");
                selections.add(totalClicks);
            } else if (metric.equals("ctr")) {

                Expression totalClicks = builder.sum(root.get("clicks"));
                Expression totalClicksPrecision = builder.prod(totalClicks, 1.0000D);

                Expression sumImpressions = builder.sum(root.get("impressions"));
                Expression sumImpressionsPrecision = builder.prod(sumImpressions, 1.0000D);

                // Find Percents
                Expression rawCtr = builder.quot(totalClicksPrecision, sumImpressionsPrecision);
                Selection ctr = builder.prod(rawCtr, 100).alias("ctr");


                selections.add(ctr);
            } else if (metric.equals("impressions")) {
                Selection totalClicks = builder.sum(root.get("impressions")).as(Integer.class).alias("impressions");
                selections.add(totalClicks);
            }
        }
    }
}
