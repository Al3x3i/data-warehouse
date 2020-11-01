package com.data.warehouse.report;

import com.data.warehouse.report.ReportResult.StatisticResponse;
import com.data.warehouse.report.WarehouseReportRequest.DimensionFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
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

    static final List<String> ALLOWED_METRICS = List.of("clicks", "ctr", "impressions");

    static final List<String> ALLOWED_DIMENSIONS = List.of("campaign", "datasource", "daily");

    public ReportResult handleRequest(WarehouseReportRequest request) {

        setDefaultFieldsIfMissing(request);

        Set metrics = request.getMetrics();
        Set dimension = request.getDimensions();
        List dimensionFilters = request.getDimensionFilters();

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        // Builder
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = builder.createTupleQuery();
        Root<Statistic> root = criteriaQuery.from(Statistic.class);


        // Append `Select`, `Group by`
        setSelectStatement(criteriaQuery, builder, root, metrics, dimension);

        // Append `Where`
        setWhereStatement(criteriaQuery, builder, root, startDate, endDate, dimensionFilters);

        // Append 'Order`
        setOrderStatement(builder, criteriaQuery);

        // Request
        List<Tuple> result = entityManager.createQuery(criteriaQuery).getResultList();

        // Mapping
        ReportResult reportResult = mapDatabaseResponseToReportResult(criteriaQuery, result);
        return reportResult;
    }

    private void setDefaultFieldsIfMissing(WarehouseReportRequest request) {
        if (isNull(request.getDimensions())) {
            request.setDimensions(new LinkedHashSet<>());
        }
        if (isNull(request.getDimensionFilters())) {
            request.setDimensionFilters(List.of());
        }
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
                        if (!isNull(tuple.get(column))) {
                            statisticResponse.setDataSource(tuple.get(column).toString());
                        }
                        break;
                    case "campaign":
                        if (!isNull(tuple.get(column))) {
                            statisticResponse.setCampaign(tuple.get(column).toString());
                        }
                        break;
                    case "totalClicks":
                        if (!isNull(tuple.get(column))) {
                            statisticResponse.setClicks(tuple.get(column).toString());
                        }
                        break;
                    case "ctr":
                        if (!isNull(tuple.get(column))) {
                            statisticResponse.setCtr(tuple.get(column).toString());
                        }
                        break;
                    case "daily":
                        if (!isNull(tuple.get(column))) {
                            statisticResponse.setDaily(tuple.get(column).toString());
                        }
                        break;
                    case "impressions":
                        if (!isNull(tuple.get(column))) {
                            statisticResponse.setImpressions(tuple.get(column).toString());
                        }
                        break;
                }
            });
            reportResult.getStatistics().add(statisticResponse);
        }
        return reportResult;

    }

    private void setWhereStatement(CriteriaQuery criteriaQuery, CriteriaBuilder builder, Root<Statistic> root,
                                   LocalDate startDate, LocalDate endDate, List<DimensionFilter> dimensionFilters) {

        List<Predicate> filterPredicates = new ArrayList<>();
        dimensionFilters.stream().forEach(filterDimension -> {
            var dimension = filterDimension.getDimension();
            var filterValue = filterDimension.getFilter();

            Expression expression = root.get(dimension);
            Predicate predicate = builder.notEqual(expression, filterValue);
            filterPredicates.add(predicate);
        });

        filterPredicates.add(builder.between(root.get("daily"), startDate, endDate));
        Predicate[] p = filterPredicates.toArray(Predicate[]::new);
        criteriaQuery.where(p);
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
