package com.u.know.loans.service;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.repository.PartnerRepository;
import com.u.know.loans.service.assembler.PartnerAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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
                .doOnNext(saved -> log.info("New partner created -> {}", saved))
                .map(assembler::toResponse);
    }

    public Mono<PartnerResponse> getPartner(Integer id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Partner with id " + id + " not found")))
                .doOnError(e -> log.error("Partner with id {} not found", id))
                .doOnNext(found ->
                        log.info("Partner with id {} found: {}",
                                found.getId(),
                                found.getName()
                        ))
                .map(assembler::toResponse);
    }

    public Mono<PartnerResponse> updatePartner(Integer id, PartnerRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Partner with id " + id + " not found for editing")))
                .doOnError(e -> log.error("Partner with id {} not found for editing", id))
                .doOnNext(found ->
                        log.info("Partner with id {} found for editing: {}",
                                found.getId(),
                                found.getName()
                        ))
                .flatMap(partner -> {
                    partner.setName(request.name());
                    return repository.save(partner);
                })
                .map(assembler::toResponse);
    }

    public Flux<PartnerResponse> getPartners() {
        return repository.findAll()
                .map(assembler::toResponse);
    }

}
