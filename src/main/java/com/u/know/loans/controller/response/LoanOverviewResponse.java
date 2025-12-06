package com.u.know.loans.controller.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record LoanOverviewResponse (
        Integer loanId,
        Integer majorityPartnerId,
        String majorityPartner,
        Integer minorityPartnerId,
        String minorityPartner,
        Integer borrowerId,
        String borrowerName,
        BigDecimal principal,
        Integer numberOfPayments,
        BigDecimal interestRate,
        BigDecimal futureValue,
        BigDecimal totalPayments,
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
        LocalDateTime insertDate,
        String comments,
        Integer planVersion){ }
