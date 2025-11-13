package com.u.know.loans.controller;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.service.PartnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest(controllers = PartnerController.class)
class PartnerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PartnerService service;

    private PartnerRequest partnerRequest;

    private PartnerResponse partnerResponse;

    @BeforeEach
    public void setup() {
        partnerRequest = new PartnerRequest("Partner Test");
        partnerResponse = new PartnerResponse(1, "Partner Test");
    }

    @Test
    void create() {
        Mockito.when(service.savePartner(Mockito.any())).thenReturn(Mono.just(partnerResponse));
        webTestClient.post()
                .uri("/api/partners")
                .bodyValue(partnerRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(partnerResponse.id());
    }

    @Test
    void create_nullRequestBody() {
        webTestClient.post()
                .uri("/api/partners")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void read() {
        Mockito.when(service.getPartner(Mockito.any())).thenReturn(Mono.just(partnerResponse));
        webTestClient.get()
                .uri("/api/partners/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(partnerResponse.id());
    }

    @Test
    void read_NotFoundException() {
        Mockito.when(service.getPartner(Mockito.any())).thenReturn(Mono.error(new NotFoundException("Partner with id 1 does not exist")));
        webTestClient.get()
                .uri("/api/partners/1")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void update() {
        Mockito.when(service.updatePartner(Mockito.anyInt(), Mockito.any())).thenReturn(Mono.just(partnerResponse));
        webTestClient.put()
                .uri("/api/partners/1")
                .bodyValue(partnerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(partnerResponse.id());
    }

    @Test
    void update_NotFoundException() {
        Mockito.when(service.updatePartner(Mockito.anyInt(), Mockito.any())).thenReturn(Mono.error(new NotFoundException("Partner with id 1 does not exist")));
        webTestClient.put()
                .uri("/api/partners/1")
                .bodyValue(partnerRequest)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void readAll() {
        Mockito.when(service.getPartners()).thenReturn(
                Flux.fromIterable(List.of(
                        new PartnerResponse(1,"Partner test 1"),
                        new PartnerResponse(1, "Partner test 2"))));

        webTestClient.get()
                .uri("/api/partners")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PartnerResponse.class)
                .hasSize(2);
    }

}