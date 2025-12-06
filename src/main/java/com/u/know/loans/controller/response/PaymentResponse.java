package com.u.know.loans.controller.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record PaymentResponse (
        Integer loanId,
        BigDecimal amount,
        BigDecimal totalPayments,
        BigDecimal statementBalance,
        LocalDate paymentDate) { }