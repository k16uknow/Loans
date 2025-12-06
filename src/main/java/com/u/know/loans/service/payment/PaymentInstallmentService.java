package com.u.know.loans.service.payment;

import com.u.know.loans.controller.request.PaymentRequest;
import com.u.know.loans.controller.response.PaymentResponse;
import com.u.know.loans.domain.Installment;
import com.u.know.loans.domain.Loan;
import com.u.know.loans.domain.Payment;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.exception.TransactionException;
import com.u.know.loans.repository.InstallmentRepository;
import com.u.know.loans.repository.LoanRepository;
import com.u.know.loans.repository.PaymentRepository;
import com.u.know.loans.service.assembler.PaymentAssembler;
import com.u.know.loans.service.enums.InstallmentStatusEnum;
import com.u.know.loans.service.payment.utils.PaymentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service("installmentPaymentService")
public class PaymentInstallmentService implements PaymentServiceInterface{

    private final PaymentRepository repository;

    private final PaymentAssembler assembler;

    private final LoanRepository loanRepository;

    private final InstallmentRepository installmentRepository;

    private final TransactionalOperator transactional;

    public PaymentInstallmentService(PaymentRepository repository,
                                     PaymentAssembler assembler,
                                     LoanRepository loanRepository,
                                     InstallmentRepository installmentRepository, TransactionalOperator transactional) {
        this.repository = repository;
        this.assembler = assembler;
        this.loanRepository = loanRepository;
        this.installmentRepository = installmentRepository;
        this.transactional = transactional;
    }

    public Mono<PaymentResponse> savePayment(Integer loanId, Mono<PaymentRequest> requestMono) {
        return requestMono.flatMap(paymentRequest -> {
            Mono<Loan> loanMono = loanRepository.findById(loanId)
                    .switchIfEmpty(Mono.error(new NotFoundException("Loan with id " + loanId + " not found")))
                    .doOnNext(loan -> log.info("Loan {} for new payment appliance found", loan.getId()));

            LocalDate[] limits = PaymentUtils.limitsForInstallmentSearch(paymentRequest.paymentDate());

            Mono<Installment> installmentMono =
                    installmentRepository.findByLoanIdAndDueDateBetweenLimits(loanId, limits[0], limits[1])
                            .flatMap(installment -> {
                                if (installment.getAmount().compareTo(paymentRequest.amount()) < 0) {
                                    return Mono.error(new TransactionException("The payment to be saved exceeds the scheduled installment amount $" + installment.getAmount()));
                                }
                                if (!installment.getStatus().equals(InstallmentStatusEnum.ACTIVE.getValue())) {
                                    return Mono.error(new TransactionException("There is no ACTIVE applicable installment for this payment"));
                                }
                                return Mono.just(installment);
                            })
                            .switchIfEmpty(Mono.error(new NotFoundException("No applicable installment for loan with id " + loanId + " was found")))
                            .doOnNext(installment -> log.info("Installment for loan with id {} found", installment.getId()));

            return Mono.zip(loanMono, installmentMono).flatMap(tuple -> {

                Loan loan = tuple.getT1();
                Installment installment = tuple.getT2();

                Payment payment = assembler.fromRequestToBuilder(paymentRequest)
                        .loanId(loan.getId())
                        .installmentId(installment.getId())
                        .build();

                return repository.findByInstallmentId(installment.getId())
                        .map(Payment::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .flatMap(installPayments -> {
                            var totalPayment = installPayments.add(payment.getAmount());
                            if (totalPayment.compareTo(installment.getAmount()) > 0) {
                                return Mono.error(new TransactionException("The payment to be saved exceeds the scheduled installment amount $"
                                        + installment.getAmount()
                                        + ". | Installment: $" + installment.getAmount()
                                        + "  | Reported payments: $" + installPayments
                                        + "  | Expected payment: $" + installment.getAmount().subtract(installPayments)));
                            } else if (totalPayment.compareTo(installment.getAmount()) == 0) {
                                installment.setStatus(InstallmentStatusEnum.PAID.getValue());
                                return installmentRepository.save(installment);
                            }
                            return Mono.just(installment);
                        })
                        .flatMap(installment1 ->
                                repository.save(payment)
                                        .as(transactional::transactional)
                                        .map(assembler::toResponse));
            });
        });
    }

}
