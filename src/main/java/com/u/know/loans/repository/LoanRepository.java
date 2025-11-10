package com.u.know.loans.repository;

import com.u.know.loans.dto.Loan;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends R2dbcRepository<Loan, Integer> { }
