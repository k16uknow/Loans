package com.u.know.loans.controller;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.service.PartnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService service;

    public PartnerController(PartnerService service){
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<PartnerResponse>> create(@RequestBody Mono<PartnerRequest> requestMono) {
        return requestMono
                .flatMap(service::savePartner)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<PartnerResponse>> get(@PathVariable("id") Integer id) {
        return service.getPartner(id)
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<PartnerResponse>> update(@PathVariable Integer id, @RequestBody Mono<PartnerRequest> requestMono) {
        return requestMono
                .flatMap(request -> service.updatePartner(id, request))
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<PartnerResponse> get(){
        return service.getPartners();
    }
}
