package com.u.know.loans.repository.querybank;

public interface Clause {

    String getName();
    Object parse(String value);
    OperatorEnum getOperator();
}
