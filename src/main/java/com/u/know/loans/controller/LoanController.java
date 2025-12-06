package com.u.know.loans.controller;

import com.u.know.loans.controller.request.LoanRequest;
import com.u.know.loans.controller.response.LoanOverviewResponse;
import com.u.know.loans.controller.response.LoanResponse;
import com.u.know.loans.controller.response.wrapper.ApiPageResponse;
import com.u.know.loans.controller.response.wrapper.ApiResponse;
import com.u.know.loans.service.loan.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ApiResponse<LoanResponse>>> create(@RequestBody Mono<LoanRequest> requestMono) {
        return service.saveLoan(requestMono)
                .map(loanResponse -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(loanResponse)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<ApiPageResponse<LoanOverviewResponse>>> get(@RequestParam Map<String, String> filters) {
        filters.putIfAbsent("page", "1");
        filters.putIfAbsent("size", "10");
        var page = Integer.parseInt(filters.get("page"));
        var size = Integer.parseInt(filters.get("size"));
        Mono<Integer> count = service.getLoanCount(filters);
        Flux<LoanOverviewResponse> loanOverviewFlux = service.getLoans(filters);
        return Mono.zip(count, loanOverviewFlux.collectList())
                .map(tuple -> {
                    Integer total = tuple.getT1();
                    List<LoanOverviewResponse> loanOverviewResponses = tuple.getT2();
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(ApiPageResponse
                                    .page(page, size, total, loanOverviewResponses.size(), loanOverviewResponses));
                });
    }

}
