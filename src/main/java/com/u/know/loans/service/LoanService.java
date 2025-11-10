package com.u.know.loans.service;

import com.u.know.loans.controller.request.LoanRequest;
import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.controller.response.LoanResponse;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.dto.Loan;
import com.u.know.loans.repository.LoanRepository;
import com.u.know.loans.service.assembler.LoanAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LoanService {

    private final LoanRepository repository;

    private final LoanAssembler assembler;

    private final BorrowerService borrowerService;

    private final PartnerService partnerService;

    public LoanService(LoanRepository repository, LoanAssembler assembler, BorrowerService borrowerService, PartnerService partnerService) {
        this.repository = repository;
        this.assembler = assembler;
        this.borrowerService = borrowerService;
        this.partnerService = partnerService;
    }

    public Mono<LoanResponse> saveLoan(Mono<LoanRequest> requestMono) {
        return requestMono.flatMap( request -> {
            Mono<BorrowerResponse> borrowerMono = borrowerService.getBorrower(request.borrowerId());
            Mono<PartnerResponse> majPartnerMono = partnerService.getPartner(request.majorityPartnerId());
            Mono<PartnerResponse> minPartnerMono = partnerService.getPartner(request.minorityPartnerId());
            return Mono.zip(borrowerMono, majPartnerMono, minPartnerMono)
                    .flatMap(tuple ->{
                                BorrowerResponse borrower = tuple.getT1();
                                PartnerResponse majPartner = tuple.getT2();
                                PartnerResponse minPartner = tuple.getT3();
                                Loan loan = assembler.fromRequest(request);
                                loan.setBorrowerId(borrower.id());
                                loan.setMajorityPartnerId(majPartner.id());
                                loan.setMinorityPartnerId(minPartner.id());
                                return repository.save(loan).doOnNext(saved -> {
                                    log.info("New loan created -> Maj Partner: {}, Min Partner: {}, Borrower: {} {}, Principal: ${}",
                                            majPartner.name(),
                                            minPartner.name(),
                                            borrower.firstName(),
                                            borrower.paternalLast(),
                                            loan.getPrincipal());
                                });
                            });
        })
                .map(assembler::toResponse);
    }

    // this helps to understand map -> sync Transformation | flatMap -> async transformation
    public Mono<LoanResponse> saveLoanFirstApproach(Mono<LoanRequest> requestMono) {
        return requestMono.flatMap(request -> {
            Loan loan = assembler.fromRequest(request);
            return borrowerService.getBorrower(request.borrowerId()).flatMap(borrower ->
                {
                    loan.setBorrowerId(borrower.id());
                    return partnerService.getPartner(request.majorityPartnerId()).flatMap(majPartner -> {
                        loan.setMajorityPartnerId(majPartner.id());
                        return partnerService.getPartner(request.minorityPartnerId()).flatMap(minPartner -> {
                            loan.setMinorityPartnerId(minPartner.id());
                            return repository.save(loan);
                        });
                    });
                });
            }).map(assembler::toResponse);
    }

}
