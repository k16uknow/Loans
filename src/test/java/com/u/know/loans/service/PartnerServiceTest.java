package com.u.know.loans.service;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.dto.Partner;
import com.u.know.loans.repository.PartnerRepository;
import com.u.know.loans.service.assembler.PartnerAssembler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PartnerServiceTest {

    @Test
    void savePartner() {
        PartnerRepository repository = Mockito.mock(PartnerRepository.class);
        PartnerAssembler assembler = Mockito.mock(PartnerAssembler.class);
        PartnerService service = new PartnerService(repository, assembler);

        PartnerRequest partnerRequest = new PartnerRequest("Partner Test");
        Partner partner = Partner.builder().name("Partner Test").id(1).build();
        PartnerResponse partnerResponse = new PartnerResponse(1, "Partner Test");

        Mockito.when(assembler.fromRequest(Mockito.any())).thenReturn(partner);
        Mockito.when(assembler.toResponse(Mockito.any())).thenReturn(partnerResponse);
        Mockito.when(repository.save(Mockito.any(Partner.class)))
                .thenReturn(Mono.just(partner));

        StepVerifier.create(service.savePartner(partnerRequest))
                .expectNextMatches(pResponse -> pResponse.name().equals(partnerRequest.name()))
                .verifyComplete();
    }
}