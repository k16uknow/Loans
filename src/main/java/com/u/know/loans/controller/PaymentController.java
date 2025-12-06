package com.u.know.loans.controller;

import com.u.know.loans.controller.request.PaymentRequest;
import com.u.know.loans.controller.response.PaymentResponse;
import com.u.know.loans.controller.response.wrapper.ApiResponse;
import com.u.know.loans.service.payment.PaymentAdvancedInstallmentService;
import com.u.know.loans.service.payment.PaymentInstallmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/loans/{loanId}")
public class PaymentController {

    private final PaymentInstallmentService paymentInstallmentService;
    private final PaymentAdvancedInstallmentService paymentAdvancedInstallmentService;


    public PaymentController(PaymentInstallmentService service, PaymentAdvancedInstallmentService paymentAdvancedInstallmentService) {
        this.paymentInstallmentService = service;
        this.paymentAdvancedInstallmentService = paymentAdvancedInstallmentService;
    }

    @PostMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ApiResponse<PaymentResponse>>> createPayment(@PathVariable Integer loanId, @RequestBody PaymentRequest paymentRequest) {
        return paymentInstallmentService.savePayment(loanId, Mono.just(paymentRequest))
                .map(savedPayment -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(savedPayment)));
    }

    @PostMapping("/advanced-payments")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ApiResponse<List<PaymentResponse>>>> createAdvancedPayments(@PathVariable Integer loanId, @RequestBody PaymentRequest paymentRequest) {
        return paymentAdvancedInstallmentService.savePayment(loanId, Mono.just(paymentRequest))
                .collectList()
                .map(list -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(ApiResponse.success(list)));
    }

}
