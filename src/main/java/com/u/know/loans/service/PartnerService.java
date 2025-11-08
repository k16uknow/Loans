package com.u.know.loans.service;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.repository.PartnerRepository;
import com.u.know.loans.service.assembler.PartnerAssembler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PartnerService {

    private final PartnerRepository repository;

    private final PartnerAssembler assembler;

    public PartnerService(PartnerRepository repository, PartnerAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public Mono<PartnerResponse> savePartner(PartnerRequest request){
        return repository.save(assembler.fromRequest(request)).map(assembler::toResponse);
    }

}
