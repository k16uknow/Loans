package com.u.know.loans.service;

import com.u.know.loans.controller.request.LoanRequest;
import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.controller.response.LoanResponse;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.dto.Loan;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.repository.LoanRepository;
import com.u.know.loans.service.assembler.LoanAssembler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.AssertionErrors;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class LoanServiceTest {

    private LoanService service;
    private LoanRepository repository;
    private LoanAssembler assembler;
    private BorrowerService borrowerService;
    private PartnerService partnerService;

    private Loan loan;
    private LoanRequest loanRequest;
    private LoanResponse loanResponse;
    private BorrowerResponse borrowerResponse;
    private PartnerResponse majPartnerResponse;
    private PartnerResponse minPartnerResponse;


    @BeforeEach
    void setup() {
        repository = Mockito.mock(LoanRepository.class);
        assembler = Mockito.mock(LoanAssembler.class);
        borrowerService = Mockito.mock(BorrowerService.class);
        partnerService = Mockito.mock(PartnerService.class);
        service = new LoanService(repository,
                assembler,
                borrowerService,
                partnerService);

        loanRequest = LoanRequest
                .builder()
                .borrowerId(1)
                .majorityPartnerId(1)
                .minorityPartnerId(2)
                .build();

        loan = Loan
                .builder()
                .id(1)
                .borrowerId(1)
                .majorityPartnerId(1)
                .minorityPartnerId(2)
                .build();

        loanResponse = LoanResponse
                .builder()
                .id(1)
                .borrowerId(1)
                .majorityPartnerId(1)
                .minorityPartnerId(2)
                .build();

        borrowerResponse = BorrowerResponse
                .builder()
                .id(1)
                .build();

        majPartnerResponse = new PartnerResponse(1,"Majority Partner");
        minPartnerResponse = new PartnerResponse(2,"Minority Partner");

        Mockito.when(borrowerService.getBorrower(Mockito.anyInt())).thenReturn(Mono.just(borrowerResponse));
        Mockito.when(partnerService.getPartner(1)).thenReturn(Mono.just(majPartnerResponse));
        Mockito.when(partnerService.getPartner(2)).thenReturn(Mono.just(minPartnerResponse));
        Mockito.when(assembler.fromRequest(Mockito.any())).thenReturn(loan);
        Mockito.when(repository.save(Mockito.any())).thenReturn(Mono.just(loan));
        Mockito.when(assembler.toResponse(Mockito.any())).thenReturn(loanResponse);
    }

    @Test
    void saveLoan() {
        StepVerifier.create(service.saveLoan(Mono.just(loanRequest)))
                .expectNextMatches(loanResponse1 -> loanResponse1.id().equals(1))
                .verifyComplete();
    }

    @Test
    void saveLoan_PartnerNotFoud() {
        Mockito.when(partnerService.getPartner(Mockito.anyInt()))
                .thenReturn(Mono.error(new NotFoundException("Partner not found")));

        StepVerifier.create(service.saveLoan(Mono.just(loanRequest)))
                .expectErrorSatisfies( e -> {
                    AssertionErrors.assertTrue("e instance of " + e.getClass(), e instanceof NotFoundException);
                    Assertions.assertEquals("Partner not found", e.getMessage());
                })
                .verify();
    }

    @Test
    void saveLoan_BorrowerNotFound() {
        Mockito.when(borrowerService.getBorrower(Mockito.anyInt()))
                .thenReturn(Mono.error(new NotFoundException("Borrower not found")));

        StepVerifier.create(service.saveLoan(Mono.just(loanRequest)))
                .expectErrorSatisfies(e -> {
                    AssertionErrors.assertTrue("e instance of ", e instanceof NotFoundException);
                    Assertions.assertEquals("Borrower not found", e.getMessage());
                })
                .verify();
    }
}