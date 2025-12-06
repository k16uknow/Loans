package com.u.know.loans.repository.querybank;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

@Getter
public enum LoanOverviewFilterClause implements Clause {

    BORROWER_NAME("borrower_name",  String.class, v -> "%" + v + "%", OperatorEnum.LIKE);

    private final String name;
    private final Class<?> type;
    private final Function<String, ?> converter;
    private final OperatorEnum operator;

    LoanOverviewFilterClause(String name,
                             Class<?> type,
                             Function<String, ?> converter,
                             OperatorEnum operator) {
        this.name = name;
        this.type = type;
        this.converter = converter;
        this.operator = operator;
    }

    public static Optional<? extends Clause> findByLabel(String labelP) {
        return Arrays.stream(LoanOverviewFilterClause.values())
                .filter(filter -> filter.getName().equals(labelP))
                .findFirst();
    }

    public Object parse(String value) {
        return converter.apply(value);
    }

}
