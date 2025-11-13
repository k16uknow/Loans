package com.u.know.loans.service;

import com.u.know.loans.controller.request.LoanRequest;
import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.controller.response.LoanResponse;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.dto.Loan;
import com.u.know.loans.exception.TransactionException;
import com.u.know.loans.repository.InstallmentRepository;
import com.u.know.loans.repository.LoanRepository;
import com.u.know.loans.service.assembler.LoanAssembler;
import com.u.know.loans.service.utils.InstallmentGenerator;
import com.u.know.loans.service.utils.LoanHeaderGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LoanService {

    private final LoanRepository repository;

    private final LoanAssembler assembler;

    private final TransactionalOperator txOperator;

    private final BorrowerService borrowerService;

    private final PartnerService partnerService;

    private final InstallmentRepository installmentRepository;

    public LoanService(LoanRepository repository,
                       LoanAssembler assembler,
                       TransactionalOperator txOperator,
                       BorrowerService borrowerService,
                       PartnerService partnerService, InstallmentRepository installmentRepository) {
        this.repository = repository;
        this.assembler = assembler;
        this.txOperator = txOperator;
        this.borrowerService = borrowerService;
        this.partnerService = partnerService;
        this.installmentRepository = installmentRepository;
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
                                LoanHeaderGenerator.fillLoanHeader(loan, borrower, majPartner.id(), minPartner.id());
                                return repository.save(loan)
                                        .flatMap(savedLoan -> {
                                            savedLoan.setConceptRequired("P" + savedLoan.getId() + borrower.firstName() + " " +  borrower.paternalLast());
                                            return repository.save(loan)
                                                    .flatMap(updatedLoan ->
                                                            installmentRepository.saveAll(InstallmentGenerator.generateInstallmentsFirstPlanVersion(savedLoan))
                                                                    .then(Mono.just(savedLoan)));
                                        })
                                        .as(txOperator::transactional)
                                        .doOnNext(saved -> log.info("New loan created -> Maj Partner: {}, Min Partner: {}, Borrower: {} {}, Principal: ${}",
                                                majPartner.name(),
                                                minPartner.name(),
                                                borrower.firstName(),
                                                borrower.paternalLast(),
                                                loan.getPrincipal()))
                                        .doOnError(e -> log.error("New Loan could not be stored due to : {}", e.getMessage()))
                                        .onErrorMap(e -> new TransactionException("New Loan could not be stored due to an Internal Server Error"));
                    });
        }).map(assembler::toResponse);
    }

}
