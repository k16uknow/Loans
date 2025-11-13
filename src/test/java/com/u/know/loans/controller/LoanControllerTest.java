package com.u.know.loans.controller;

import com.u.know.loans.controller.request.LoanRequest;
import com.u.know.loans.controller.response.LoanResponse;
import com.u.know.loans.exception.BadRequestException;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = LoanController.class)
class LoanControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private LoanService service;

    private LoanResponse loanResponse;

    @BeforeEach
    void setup() {
        Mockito.when(service.saveLoan(Mockito.any()))
                .thenReturn(Mono
                        .just(LoanResponse
                                .builder()
                                .id(1)
                                .build()
                ));
    }

    @Test
    void create() {
        webTestClient.post()
                .uri("/api/loans")
                .bodyValue(LoanRequest.builder().build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(1);
    }

    @Test
    void create_DependencyNotFoud() {
        Mockito.when(service.saveLoan(Mockito.any())).thenReturn(Mono.error(new NotFoundException("Dependency not Found")));

        webTestClient.post()
                .uri("/api/loans")
                .bodyValue(LoanRequest.builder().build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.error.message").isEqualTo("Dependency not Found");
    }

    @Test
    void create_BadRequest() {
        Mockito.when(service.saveLoan(Mockito.any())).thenReturn(Mono.error(new BadRequestException("Bad request exception")));
        webTestClient.post()
                .uri("/api/loans")
                .bodyValue(LoanRequest.builder().build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.error.message").isEqualTo("Bad request exception");
    }

}