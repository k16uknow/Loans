package com.u.know.loans.service;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.repository.PartnerRepository;
import com.u.know.loans.service.assembler.PartnerAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class PartnerService {

    private final PartnerRepository repository;

    private final PartnerAssembler assembler;

    public PartnerService(PartnerRepository repository, PartnerAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public Mono<PartnerResponse> savePartner(PartnerRequest request){
        return repository.save(assembler.fromRequest(request))
                .doOnNext(saved -> log.info("Saved partner :{}", saved))
                .map(assembler::toResponse);
    }

}
