package com.u.know.loans.service.payment;

import com.u.know.loans.controller.request.PaymentRequest;
import com.u.know.loans.controller.response.PaymentResponse;
import com.u.know.loans.domain.Installment;
import com.u.know.loans.domain.Payment;
import com.u.know.loans.exception.TransactionException;
import com.u.know.loans.repository.PaymentRepository;
import com.u.know.loans.service.assembler.PaymentAssembler;
import com.u.know.loans.service.enums.InstallmentStatusEnum;
import com.u.know.loans.service.installment.InstallmentUtilitiesService;
import com.u.know.loans.service.loan.LoanUtilitiesService;
import com.u.know.loans.service.payment.utils.PaymentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PaymentAdvancedInstallmentService implements PaymentServiceInterface{

    private final PaymentRepository repository;
    private final PaymentAssembler assembler;
    private final LoanUtilitiesService loanUtilitiesService;
    private final InstallmentUtilitiesService installmentUtilitiesService;

    public PaymentAdvancedInstallmentService(PaymentRepository repository,
                                             PaymentAssembler assembler,
                                             LoanUtilitiesService loanUtilitiesService,
                                             InstallmentUtilitiesService installmentUtilitiesService) {
        this.repository = repository;
        this.assembler = assembler;
        this.loanUtilitiesService = loanUtilitiesService;
        this.installmentUtilitiesService = installmentUtilitiesService;
    }

    public Flux<PaymentResponse> savePayment(Integer loanId, Mono<PaymentRequest> requestMono) {
        return requestMono
                .flatMapMany(paymentRequest -> loanUtilitiesService.getLoanById(loanId)
                        .flatMapMany(loan -> installmentUtilitiesService
                                .getApplicableInstallmentsByLoanId(loanId)
                                .collectList()
                                .flatMapMany(installmentList -> {
                                    if (installmentList.isEmpty()) {
                                        return Flux.error(new TransactionException("There is no ACTIVE applicable installment for this payment"));
                                    }
                                    var applicablePayments = PaymentUtils.applicableInstallmentsForPayment(paymentRequest.amount(), installmentList);
                                    if (installmentList.size() < applicablePayments) {
                                        return Flux.error(new TransactionException("The payment amount exceeds remaining installment payments"));
                                    }
                                    if (installmentList.size() > applicablePayments) {
                                        installmentList = installmentList.subList(0, applicablePayments);
                                    }
                                    List<Payment> paymentList = new ArrayList<>();
                                    BigDecimal requestPaymentAmount = paymentRequest.amount();
                                    for (var installment : installmentList) {
                                        var payment = assembler.fromRequest(paymentRequest);
                                        payment.setLoanId(loan.getId());
                                        payment.setInstallmentId(installment.getId());
                                        payment.setPaymentDate(LocalDate.now());
                                        BigDecimal paidAmountPerInstallment = installment.getAmount().subtract(installment.getPaidAmount());
                                        payment.setAmount(requestPaymentAmount.compareTo(paidAmountPerInstallment) > 0
                                                ? paidAmountPerInstallment
                                                : requestPaymentAmount);
                                        requestPaymentAmount = requestPaymentAmount.subtract(paidAmountPerInstallment);
                                        paymentList.add(payment);
                                        installment.setPaidAmount(installment.getPaidAmount().add(payment.getAmount()));
                                        if (installment.getAmount().equals(installment.getPaidAmount())) {
                                            installment.setStatus(InstallmentStatusEnum.PAID.getValue());
                                        }
                                    }
                                    List<Installment> finalInstallmentList = installmentList;
                                    return repository.saveAll(Flux.fromIterable(paymentList))
                                            .collectList()
                                            .flatMapMany(payments -> installmentUtilitiesService
                                                    .saveAll(Flux.fromIterable(finalInstallmentList))
                                                    .thenMany(Flux.fromIterable(payments))
                                            )
                                            .map(assembler::toResponse);
                                })
                        )
                );
    }

}
