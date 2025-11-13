package com.u.know.loans.controller.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InstallmentResponse(
        Integer id,
        Integer loanId,
        Integer installmentNo,
        LocalDate dueDate,
        BigDecimal amount,
        BigDecimal totalPayments,
        BigDecimal statementBalance) { }
