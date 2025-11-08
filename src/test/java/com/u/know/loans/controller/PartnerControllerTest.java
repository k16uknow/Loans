package com.u.know.loans.controller;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PartnerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void create() {
        PartnerRequest partnerRequest = new PartnerRequest("Partner Test");

        webTestClient.post()
                .uri("/api/partners")
                .bodyValue(partnerRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PartnerResponse.class);

    }
}