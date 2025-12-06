package com.u.know.loans.service.loan;

import com.u.know.loans.domain.Loan;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.repository.LoanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service("loanUtilitiesService")
public class LoanUtilitiesService implements LoanServiceInterface {

    private final LoanRepository repository;

    public LoanUtilitiesService(LoanRepository repository) {
        this.repository = repository;
    }

    public Mono<Loan> getLoanById(Integer loanId) {
        return repository.findById(loanId)
                .switchIfEmpty(Mono.error(new NotFoundException("Loan with id " + loanId + " not found")))
                .doOnNext(loan -> log.info("Loan {} for new payment appliance found", loan.getId()));
    }

}
