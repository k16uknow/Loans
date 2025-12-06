package com.u.know.loans.repository.querybank;

import lombok.Getter;

@Getter
public enum OperatorEnum {

    EQUALS(" = "),
    LIKE(" LIKE ");

    private final String sql;

    OperatorEnum(String sql) {
        this.sql = sql;
    }
}
