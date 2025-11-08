package com.u.know.loans.controller;

import com.u.know.loans.service.PartnerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PartnerController {

    private final PartnerService service;

    public PartnerController(PartnerService service){
        this.service = service;
    }

//    @PostMapping()
//    @ResponseStatus(HttpStatus.CREATED)
//    public Mono<>
}
