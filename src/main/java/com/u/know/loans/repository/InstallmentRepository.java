package com.u.know.loans.repository;

import com.u.know.loans.dto.Installment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentRepository extends R2dbcRepository<Installment, Integer> { }
