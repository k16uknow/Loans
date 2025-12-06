package com.u.know.loans.repository;

import com.u.know.loans.domain.Installment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface InstallmentRepository extends R2dbcRepository<Installment, Integer> {

    @Query("""
            SELECT * FROM installment i
            WHERE i.loan_id = :loanId
            AND i.due_date >= :paymentDateLowLimit
            AND i.due_date <= :paymentDateHighLimit
            LIMIT 1
            """)
    Mono<Installment> findByLoanIdAndDueDateBetweenLimits(@Param("loanId") Integer loanId,
                                                          @Param("paymentDateLowLimit") LocalDate lowLimit,
                                                          @Param("paymentDateHighLimit") LocalDate highLimit);

    Flux<Installment> findByLoanIdAndStatusOrderByInstallmentNo(@Param("loanId") Integer loanId, @Param("status") String status);
}
