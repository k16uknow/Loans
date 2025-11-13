package com.u.know.loans.controller.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record LoanRequest (
        Integer majorityPartnerId,
        Integer minorityPartnerId,
        Integer borrowerId,
        BigDecimal principal,
        Integer numberOfPayments,
        BigDecimal interestRate,
        LocalDate releaseDate,
        BigDecimal minorityPartnerPct,
        String comments) { }
