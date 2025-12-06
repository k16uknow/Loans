package com.u.know.loans.repository.querybank;

import java.util.*;
import java.util.function.Function;

import static com.u.know.loans.repository.querybank.QueryClause.LIMIT;
import static com.u.know.loans.repository.querybank.QueryClause.OFFSET;

public class QueryGenerator {

    public static QueryAndFiltersAndClauses generateQuery(Query queryEnum,
                                                          Map<String, String> filters,
                                                          Function<String, Optional<? extends Clause>> resolverForFilterClause) {

        Map<Clause, Object> allClauseMap = new HashMap<>();
        var query = new StringBuilder(queryEnum.getQuery());
        List<QueryClause> queryClauses = new ArrayList<>();
        boolean first = true;
        for(String key : filters.keySet()) {
            Optional<? extends Clause> optionalClause = resolverForFilterClause.apply(key);
            if(optionalClause.isEmpty()) {
                Optional<QueryClause> queryClauseOptional = (Optional<QueryClause>) QueryClause.findByRequestParam(key);
                queryClauseOptional.ifPresent(queryClauses::add);
                continue;
            }
            var clause = optionalClause.get();
            allClauseMap.put(clause, clause.parse(filters.get(key)));
            if(first) {
                query.append(" WHERE ");
                first = false;
            } else {
                query.append(" AND ");
            }
            query.append(clause.getName())
                    .append(clause.getOperator().getSql())
                    .append(":")
                    .append(clause.getName());
        }
        if(query.indexOf("COUNT(*)") == -1 ) {
            Map<Clause, Object> clauseMap = clauseMap(queryClauses, filters, query);
            allClauseMap.putAll(clauseMap);
        }
        return new QueryAndFiltersAndClauses(query.toString(), allClauseMap);
    }

    private static Map<Clause, Object> clauseMap(List<QueryClause> clauses, Map<String, String> filters, StringBuilder query) {
        Map<Clause, Object> clauseMap = new HashMap<>();
        clauses.sort(Comparator.comparing(QueryClause::getClauseOrder));
        for (var clause : clauses) {
            if (clause.equals(QueryClause.ORDER_BY_ASC) || clause.equals(QueryClause.ORDER_BY_DESC)){
                query.append(clause.getInitial_clause())
                        .append(clause.parse(filters.get(clause.getRequestParam())))
                        .append(clause.getFinal_clause());
            } else {
                clauseMap.put(clause, clause.parse(filters.get(clause.getRequestParam())));
                query.append(clause.getInitial_clause())
                        .append(":")
                        .append(clause.getName())
                        .append(clause.getFinal_clause());
            }
        }
        clauseMap.putIfAbsent(LIMIT, 0);
        clauseMap.putIfAbsent(OFFSET, 10);
        clauseMap.put(OFFSET, (Integer) clauseMap.get(OFFSET) * (Integer) clauseMap.get(LIMIT) );
        return clauseMap;
    }

}
