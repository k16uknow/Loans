package com.u.know.loans.service.installment;

import com.u.know.loans.domain.Installment;
import com.u.know.loans.repository.InstallmentRepository;
import com.u.know.loans.service.enums.InstallmentStatusEnum;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service("installmentUtilitiesService")
public class InstallmentUtilitiesService implements InstallmentServiceInterface {

    private final InstallmentRepository repository;

    InstallmentUtilitiesService(InstallmentRepository repository) {
        this.repository = repository;
    }

    public Flux<Installment> getApplicableInstallmentsByLoanId(Integer loanId) {
        return repository.findByLoanIdAndStatusOrderByInstallmentNo(loanId, InstallmentStatusEnum.ACTIVE.getValue());
    }

    public Flux<Installment> saveAll(Flux<Installment> installmentFlux) {
        return repository.saveAll(installmentFlux);
    }

}
