package com.u.know.loans.controller;

import com.u.know.loans.controller.request.LoanRequest;
import com.u.know.loans.controller.response.LoanResponse;
import com.u.know.loans.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ApiResponse<LoanResponse>>> create(@RequestBody Mono<LoanRequest> requestMono) {
        return service.saveLoan(requestMono)
                .map(loanResponse -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(loanResponse)));
    }
}
