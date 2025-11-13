package com.u.know.loans.controller;

import com.u.know.loans.controller.request.BorrowerRequest;
import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.service.BorrowerService;
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

@WebFluxTest(BorrowerController.class)
class BorrowerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BorrowerService service;

    private BorrowerRequest borrowerRequest;

    private BorrowerResponse borrowerResponse;

    @BeforeEach
    public void setup() {
        borrowerRequest = BorrowerRequest.builder()
                .firstName("Borrower test")
                .build();

        borrowerResponse = BorrowerResponse
                .builder()
                .id(1)
                .firstName("Borrower test")
                .build();
    }

    @Test
    void create() {
        Mockito.when(service.saveBorrower(Mockito.any()))
                        .thenReturn(Mono.just(borrowerResponse));

        webTestClient.post()
                .uri("/api/borrowers")
                .bodyValue(borrowerRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(borrowerResponse.id());

    }

    @Test
    void create_nullRequestBody() {
        webTestClient.post()
                .uri("/api/borrowers")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void read() {
        Mockito.when(service.getBorrower(1)).thenReturn(Mono.just(borrowerResponse));
        webTestClient.get()
                .uri("/api/borrowers/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(borrowerResponse.id());

    }

    @Test
    void read_NotFoundException() {
        Mockito.when(service.getBorrower(0))
                .thenReturn(Mono.error(new NotFoundException("Borrower with id 0 not found")));

        webTestClient.get()
                .uri("/api/borrowers/0")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.data").isEqualTo(null);
    }

    @Test
    void update() {
        Mockito.when(service.updateBorrower(1, borrowerRequest)).thenReturn(Mono.just(borrowerResponse));
        webTestClient.put()
                .uri("/api/borrowers/1")
                .bodyValue(borrowerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.firstName").isEqualTo(borrowerResponse.firstName());
    }

    @Test
    void update_NotFound() {
        Mockito.when(service.updateBorrower(0, borrowerRequest)).thenReturn(Mono.error(new NotFoundException("Borrower with id 0 not found")));
        webTestClient.put()
                .uri("/api/borrowers/0")
                .bodyValue(borrowerRequest)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.data").isEqualTo(null);
    }

    @Test
    void readAll() {
        Mockito.when(service.getBorrowers()).thenReturn(Flux.fromIterable(
                List.of(
                        BorrowerResponse.builder().id(1).build(),
                        BorrowerResponse.builder().id(2).build(),
                        BorrowerResponse.builder().id(3).build()
                )
        ));
        webTestClient.get()
                .uri("/api/borrowers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BorrowerResponse.class)
                .hasSize(3);
    }

}