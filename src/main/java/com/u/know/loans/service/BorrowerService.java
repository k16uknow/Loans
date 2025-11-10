package com.u.know.loans.service;

import com.u.know.loans.controller.request.BorrowerRequest;
import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.repository.BorrowerRepository;
import com.u.know.loans.service.assembler.BorrowerAssembler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BorrowerService {

    private final BorrowerRepository repository;

    private final BorrowerAssembler assembler;

    public BorrowerService(BorrowerRepository repository, BorrowerAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public Mono<BorrowerResponse> saveBorrower(BorrowerRequest request) {
        return repository.save(assembler.fromRequest(request))
                .doOnNext(saved -> log.info("New borrower created -> {}", saved))
                .map(assembler::toResponse);
    }

    public Mono<BorrowerResponse> getBorrower(Integer id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Borrower with id " + id +" not found")))
                .doOnNext(found ->
                        log.info("Borrower with id {} found: {}",
                                found.getId(),
                                found.getFirstName() + " " + found.getPaternalLast()
                        ))
                .map(assembler::toResponse);

    }

    public Mono<BorrowerResponse> updateBorrower(Integer id, BorrowerRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Borrower with id " + id +" not found")))
                .doOnNext(found ->
                        log.info("Borrower with id {} found for editing: {}",
                                found.getId(),
                                found.getFirstName() + " " + found.getPaternalLast()
                        ))
                .flatMap(borrower -> {
                    assembler.updateFromRequest(borrower, request);
                    return repository.save(borrower);
                })
                .map(assembler::toResponse);
    }

    public Flux<BorrowerResponse> getBorrowers() {
        return repository.findAll()
                .map(assembler::toResponse);
    }
}
