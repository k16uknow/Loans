package com.u.know.loans.repository.querybank;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@Getter
public enum QueryClause implements Clause {

    ORDER_BY_ASC("ORDER_BY_ASC", " ORDER BY ", " ASC", 1 , String.class, v -> v, "asc"),
    ORDER_BY_DESC("ORDER_BY_DESC"," ORDER BY ", " DESC", 1,  String.class, v -> v, "desc"),
    LIMIT("LIMIT"," LIMIT ","", 2, Integer.class, Integer::parseInt, "size"),
    OFFSET("OFFSET"," OFFSET ", "", 3, Integer.class, i -> Integer.parseInt(i) - 1, "page");

    private final String name;
    private final String initial_clause;
    private final String final_clause;
    private final Integer clauseOrder;
    private final Class<?> type;
    private final Function<String, ?> converter;
    private final String requestParam;

    QueryClause(String name, String initial_clause, String final_clause, Integer clauseOrder, Class<?> type, Function<String, ?> converter, String requestParam) {
        this.name = name;
        this.initial_clause = initial_clause;
        this.final_clause = final_clause;
        this.clauseOrder = clauseOrder;
        this.type = type;
        this.converter = converter;
        this.requestParam = requestParam;
    }

    public static Optional<? extends Clause> findByRequestParam(String requestParam) {
        return Arrays.stream(QueryClause.values())
                .filter(clause -> clause.getRequestParam().equals(requestParam))
                .findFirst();
    }

    @Override
    public Object parse(String value) {
        return converter.apply(value);
    }

    @Override
    public OperatorEnum getOperator() {
        return null;
    }

}
