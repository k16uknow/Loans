package com.u.know.loans.service;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.dto.Partner;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.repository.PartnerRepository;
import com.u.know.loans.service.assembler.PartnerAssembler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class PartnerServiceTest {

    private PartnerService service;
    private PartnerRepository repository;
    private PartnerAssembler assembler;

    private PartnerRequest partnerRequest;
    private Partner partner;
    private PartnerResponse partnerResponse;

    @BeforeEach
    public void setup() {
        repository = Mockito.mock(PartnerRepository.class);
        assembler = Mockito.mock(PartnerAssembler.class);
        service = new PartnerService(repository, assembler);

        partnerRequest = new PartnerRequest("Partner Test");
        partner = Partner.builder().name("Partner Test").id(1).build();
        partnerResponse = new PartnerResponse(1, "Partner Test");
        Mockito.when(repository.findById(1)).thenReturn(Mono.just(partner));
        Mockito.when(repository.save(Mockito.any(Partner.class)))
                .thenReturn(Mono.just(partner));
    }

    @Test
    void savePartner() {
        Mockito.when(assembler.fromRequest(Mockito.any())).thenReturn(partner);
        Mockito.when(assembler.toResponse(Mockito.any())).thenReturn(partnerResponse);

        StepVerifier.create(service.savePartner(partnerRequest))
                .expectNextMatches(pResponse -> pResponse.name().equals(partnerRequest.name()))
                .verifyComplete();
    }

    @Test
    void getPartner() {
        Mockito.when(repository.findById(Mockito.anyInt()))
                .thenReturn(Mono.just(partner));
        Mockito.when(assembler.toResponse(Mockito.any())).thenReturn(partnerResponse);

        StepVerifier.create(service.getPartner(1))
                .expectNextMatches(partnerResponse1 -> partnerResponse1.equals(partnerResponse))
                .verifyComplete();
    }

    @Test
    void getPartner_NotFoundException() {
        Mockito.when(repository.findById(0))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.getPartner(0))
                .expectErrorSatisfies(e -> {
                    Assertions.assertInstanceOf(NotFoundException.class, e);
                    Assertions.assertEquals("Partner with id 0 not found", e.getMessage());
                })
                .verify();
    }

    @Test
    void updatedPartner() {
        Mockito.when(assembler.toResponse(Mockito.any()))
                .thenReturn(new PartnerResponse(1, "Partner Test Updated"));


        StepVerifier.create(service.updatePartner(1, partnerRequest))
                .expectNextMatches(uPartnerResponse -> uPartnerResponse.name().equals("Partner Test Updated"))
                .verifyComplete();

    }

    @Test
    void updatedPartner_NotFoundException() {
        PartnerRequest updatedPartnerRequest = new PartnerRequest("Partner Test Updated");
        Mockito.when(repository.findById(0))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.updatePartner(0, updatedPartnerRequest))
                .expectErrorSatisfies(e -> {
                    Assertions.assertInstanceOf(NotFoundException.class, e);
                    Assertions.assertEquals("Partner with id 0 not found for editing", e.getMessage());
                })
                .verify();

    }

    @Test
    void getPartners() {
        Mockito.when(repository.findAll()).thenReturn(Flux.fromIterable(List.of(
                new Partner(1,"Partner test 1"),
                new Partner(1, "Partner test 2"))));

        Mockito.when(assembler.toResponse(Mockito.any())).thenReturn(
                new PartnerResponse(1,"Partner test")
        );

        StepVerifier.create(service.getPartners())
                .expectNextMatches(partnerResponse -> partnerResponse.id().equals(1))
                .expectNextCount(1) // expect 1 more item
                .verifyComplete();

    }

}