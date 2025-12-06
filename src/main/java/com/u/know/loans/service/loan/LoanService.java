package com.u.know.loans.service.loan;

import com.u.know.loans.controller.request.LoanRequest;
import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.controller.response.LoanOverviewResponse;
import com.u.know.loans.controller.response.LoanResponse;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.domain.Loan;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.exception.TransactionException;
import com.u.know.loans.repository.InstallmentRepository;
import com.u.know.loans.repository.LoanRepository;
import com.u.know.loans.repository.querybank.*;
import com.u.know.loans.service.BorrowerService;
import com.u.know.loans.service.PartnerService;
import com.u.know.loans.service.assembler.LoanAssembler;
import com.u.know.loans.service.assembler.LoanOverviewAssembler;
import com.u.know.loans.service.utils.InstallmentGenerator;
import com.u.know.loans.service.utils.LoanHeaderGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Map;

import static com.u.know.loans.repository.querybank.Query.GET_LOAN_OVERVIEWS;
import static com.u.know.loans.repository.querybank.Query.GET_TOTAL_LOAN_COUNT;

@Slf4j
@Service
public class LoanService implements LoanServiceInterface {

    private final LoanRepository repository;

    private final DatabaseClient dbClient;

    private final LoanAssembler assembler;

    private final LoanOverviewAssembler loanOverviewAssembler;

    private final TransactionalOperator txOperator;

    private final BorrowerService borrowerService;

    private final PartnerService partnerService;

    private final InstallmentRepository installmentRepository;

    public LoanService(LoanRepository repository,
                       DatabaseClient dbClient,
                       LoanAssembler assembler,
                       LoanOverviewAssembler loanOverviewAssembler,
                        TransactionalOperator txOperator,
                        BorrowerService borrowerService,
                        PartnerService partnerService,
                        InstallmentRepository installmentRepository) {
        this.repository = repository;
        this.dbClient = dbClient;
        this.assembler = assembler;
        this.loanOverviewAssembler = loanOverviewAssembler;
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

    public Mono<Integer> getLoanCount(Map<String, String> filters) {
        QueryAndFiltersAndClauses queryAndFiltersAndClauses = QueryGenerator
                .generateQuery(GET_TOTAL_LOAN_COUNT, filters, LoanOverviewFilterClause::findByLabel);
        var sql = dbClient.sql(queryAndFiltersAndClauses.query());
        for(Clause clause : queryAndFiltersAndClauses.filters().keySet()) {
            sql = sql.bind(clause.getName(), queryAndFiltersAndClauses.filters().get(clause));
        }
        return sql.map(row -> row.get(0, Integer.class))
                .one()
                .switchIfEmpty(Mono.error(new NotFoundException("No results for loans were found")));
    }

    public Flux<LoanOverviewResponse> getLoans(Map<String, String> filters) {

        QueryAndFiltersAndClauses queryAndFiltersAndClauses = QueryGenerator
                .generateQuery(GET_LOAN_OVERVIEWS, filters, LoanOverviewFilterClause::findByLabel);
        var sql = dbClient.sql(queryAndFiltersAndClauses.query());
        for(Clause clause : queryAndFiltersAndClauses.filters().keySet()) {
            sql = sql.bind(clause.getName(), queryAndFiltersAndClauses.filters().get(clause));
        }

        return sql.map(loanOverviewAssembler::fromRow)
                .all()
                .map(loanOverviewAssembler::toResponse)
                .switchIfEmpty(Flux.fromIterable(new ArrayList<LoanOverviewResponse>()));
    }
}
