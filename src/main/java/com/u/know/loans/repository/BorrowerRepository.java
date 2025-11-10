package com.u.know.loans.repository;

import com.u.know.loans.dto.Borrower;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowerRepository extends R2dbcRepository<Borrower, Integer> { }
