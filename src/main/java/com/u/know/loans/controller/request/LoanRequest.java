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
        BigDecimal futureValue,
        LocalDate releaseDate,
        LocalDate firstPaymentDate,
        LocalDate lastPaymentDate,
        BigDecimal grossProfit,
        BigDecimal majorityPartnerPct,
        BigDecimal majorityPartnerProfit,
        BigDecimal minorityPartnerPct,
        BigDecimal minorityPartnerProfit,
        String conceptRequired,
        BigDecimal axen,
        LocalDate insertDate,
        String comments) { }
