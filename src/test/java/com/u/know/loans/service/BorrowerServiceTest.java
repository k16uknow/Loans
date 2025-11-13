package com.u.know.loans.service;

import com.u.know.loans.controller.request.BorrowerRequest;
import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.dto.Borrower;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.repository.BorrowerRepository;
import com.u.know.loans.service.assembler.BorrowerAssembler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class BorrowerServiceTest {

    private BorrowerService service;
    private BorrowerRepository repository;
    private BorrowerAssembler assembler;

    private Borrower borrower;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(BorrowerRepository.class);
        assembler = Mockito.mock(BorrowerAssembler.class);
        service = new BorrowerService(repository, assembler);

        borrower = Borrower
                .builder()
                .id(1)
                .build();

        Mockito.when(assembler.fromRequest(Mockito.any())).thenReturn(borrower);
        Mockito.when(repository.save(Mockito.any())).thenReturn(Mono.just(borrower));
        Mockito.when(assembler.toResponse(Mockito.any())).thenReturn(BorrowerResponse
                .builder()
                .id(1)
                .build());
        Mockito.when(repository.findById(1)).thenReturn(Mono.just(borrower));
    }

    @Test
    void saveBorrower() {
        StepVerifier.create(service.saveBorrower(BorrowerRequest
                        .builder()
                        .build()))
                .expectNextMatches(response -> response.id().equals(1))
                .verifyComplete();
    }

    @Test
    void getBorrower() {
        StepVerifier.create(service.getBorrower(1))
                .expectNextMatches(response -> response.id().equals(1))
                .verifyComplete();
    }

    @Test
    void getBorrower_NotFoundException() {
        Mockito.when(repository.findById(0))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.getBorrower(0))
                .expectErrorSatisfies( e -> {
                    Assertions.assertInstanceOf(NotFoundException.class, e);
                    Assertions.assertEquals("Borrower with id 0 not found", e.getMessage());
                })
                .verify();
    }

    @Test
    void updateBorrower() {
        Mockito.when(assembler.toResponse(Mockito.any())).thenReturn(BorrowerResponse
                .builder()
                .firstName("Edited test name")
                .build());

        Mockito.when(assembler.updateFromRequest(Mockito.any(), Mockito.any())).thenReturn(borrower);
        StepVerifier.create(service.updateBorrower(1, BorrowerRequest.builder().firstName("Edited test name").build()))
                .expectNextMatches(response -> response.firstName().equals("Edited test name"))
                .verifyComplete();
    }

    @Test
    void updateBorrower_NotFound() {
        Mockito.when(repository.findById(0))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.updateBorrower(0, BorrowerRequest.builder().firstName("Edited test name").build()))
                .expectErrorSatisfies( e -> {
                    Assertions.assertInstanceOf(NotFoundException.class, e);
                    Assertions.assertEquals("Borrower with id 0 not found for editing", e.getMessage());
                })
                .verify();
    }

    @Test
    void getBorrowers() {
        Mockito.when(repository.findAll()).thenReturn(Flux.fromIterable(List.of(
                Borrower.builder().id(1).build(),
                Borrower.builder().id(2).build(),
                Borrower.builder().id(3).build()
        )));
        StepVerifier.create(service.getBorrowers())
                .expectNextMatches(found -> found.id().equals(1))
                .expectNextCount(2)
                .verifyComplete();
    }
}