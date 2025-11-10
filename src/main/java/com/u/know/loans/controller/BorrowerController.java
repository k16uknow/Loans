package com.u.know.loans.controller;

import com.u.know.loans.controller.request.BorrowerRequest;
import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.service.BorrowerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {

    private final BorrowerService service;

    public BorrowerController(BorrowerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ApiResponse<BorrowerResponse>>> create(@RequestBody Mono<BorrowerRequest> requestMono) {
        return requestMono
                .flatMap(service::saveBorrower)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(response)));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<ApiResponse<BorrowerResponse>>> read(@PathVariable Integer id) {
        return service.getBorrower(id)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(response)));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<BorrowerResponse> read() {
        return service.getBorrowers();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<ApiResponse<BorrowerResponse>>> update(@PathVariable Integer id, @RequestBody Mono<BorrowerRequest> requestMono) {
        return requestMono
                .flatMap( request -> service.updateBorrower(id, request))
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)));
    }
}
