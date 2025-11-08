package com.u.know.loans.repository;

import com.u.know.loans.dto.Partner;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends ReactiveCrudRepository<Partner, Integer> {}
