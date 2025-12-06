package com.u.know.loans.controller.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record PaymentRequest(
        BigDecimal amount,
        LocalDate paymentDate) { }
