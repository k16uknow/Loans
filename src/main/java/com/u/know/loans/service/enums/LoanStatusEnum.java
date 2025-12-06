package com.u.know.loans.service.enums;

import lombok.Getter;

@Getter
public enum LoanStatusEnum {

    ACTIVE("ACTIVE"),
    PAID("PAID"),
    INACTIVE("INACTIVE"),
    REPLACED("REPLACED");

    private final String value;

    LoanStatusEnum(String value) {
        this.value = value;
    }

}
