package com.u.know.loans.repository;

import com.u.know.loans.domain.Payment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PaymentRepository extends R2dbcRepository<Payment, Integer> {

    Flux<Payment> findByInstallmentId(@Param("installmentId") Integer installmentId);

    Flux<Payment> findByLoanId(@Param("loanId") Integer loanId);

}
