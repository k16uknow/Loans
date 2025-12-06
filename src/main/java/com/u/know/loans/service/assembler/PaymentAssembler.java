package com.u.know.loans.service.assembler;

import com.u.know.loans.controller.request.PaymentRequest;
import com.u.know.loans.controller.response.PaymentResponse;
import com.u.know.loans.domain.Payment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentAssembler implements Assembler<Payment, PaymentRequest, PaymentResponse> {

    @Override
    public Payment fromRequest(PaymentRequest request) {
        return Payment
                .builder()
                .amount(request.amount())
                .paymentDate(request.paymentDate())
                .insertDate(LocalDateTime.now())
                .build();
    }

    @Override
    public PaymentResponse toResponse(Payment domain) {
        return PaymentResponse
                .builder()
                .loanId(domain.getLoanId())
                .amount(domain.getAmount())
                .paymentDate(domain.getPaymentDate())
                .build();

    }

    public Payment.PaymentBuilder fromRequestToBuilder(PaymentRequest request) {
        return Payment
                .builder()
                .amount(request.amount())
                .paymentDate(request.paymentDate())
                .insertDate(LocalDateTime.now());
    }

}
