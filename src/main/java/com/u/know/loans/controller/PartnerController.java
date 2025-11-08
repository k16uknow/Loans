package com.u.know.loans.controller;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.service.PartnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
public class PartnerController {

    private final PartnerService service;

    public PartnerController(PartnerService service){
        this.service = service;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<PartnerResponse>> create(@RequestBody Mono<PartnerRequest> requestMono) {
        return requestMono
                .flatMap(service::savePartner)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

}
