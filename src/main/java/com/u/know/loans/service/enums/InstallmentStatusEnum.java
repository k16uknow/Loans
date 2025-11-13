package com.u.know.loans.service.enums;

import lombok.Getter;

@Getter
public enum InstallmentStatusEnum {

    ACTIVE("ACTIVE"),
    PAID("PAID"),
    INACTIVE("INACTIVE"),
    REPLACED("REPLACED");

    private final String value;

    InstallmentStatusEnum(String value) {
        this.value = value;
    }

}
