package com.u.know.loans.service;

import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import com.u.know.loans.domain.Partner;
import com.u.know.loans.repository.PartnerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PartnerService {

    private final PartnerRepository repository;

    public PartnerService(PartnerRepository repository) {
        this.repository = repository;
    }

    public Mono<PartnerResponse> savePartner(PartnerRequest request){
        Mono.just(request).map(Partner::new).flatMap(i -> repository.save(i)).map()
//        return repository.save(partner);
    }
}
