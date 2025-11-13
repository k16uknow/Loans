package com.u.know.loans.service.assembler;

import com.u.know.loans.controller.request.LoanRequest;
import com.u.know.loans.controller.response.LoanResponse;
import com.u.know.loans.dto.Loan;
import org.springframework.stereotype.Component;

@Component
public class LoanAssembler implements Assembler<Loan, LoanRequest, LoanResponse>{

    @Override
    public Loan fromRequest(LoanRequest request) {
        return Loan.builder()
                .majorityPartnerId(request.majorityPartnerId())
                .minorityPartnerId(request.minorityPartnerId())
                .borrowerId(request.borrowerId())
                .principal(request.principal())
                .numberOfPayments(request.numberOfPayments())
                .interestRate(request.interestRate())
                .releaseDate(request.releaseDate())
                .minorityPartnerPct(request.minorityPartnerPct())
                .comments(request.comments())
                .build();
    }

    @Override
    public LoanResponse toResponse(Loan domain) {
        return LoanResponse.builder()
                .id(domain.getId())
                .majorityPartnerId(domain.getMajorityPartnerId())
                .minorityPartnerId(domain.getMinorityPartnerId())
                .borrowerId(domain.getBorrowerId())
                .principal(domain.getPrincipal())
                .numberOfPayments(domain.getNumberOfPayments())
                .interestRate(domain.getInterestRate())
                .futureValue(domain.getFutureValue())
                .releaseDate(domain.getReleaseDate())
                .firstPaymentDate(domain.getFirstPaymentDate())
                .lastPaymentDate(domain.getLastPaymentDate())
                .grossProfit(domain.getGrossProfit())
                .majorityPartnerPct(domain.getMajorityPartnerPct())
                .majorityPartnerProfit(domain.getMajorityPartnerProfit())
                .minorityPartnerPct(domain.getMinorityPartnerPct())
                .minorityPartnerProfit(domain.getMinorityPartnerProfit())
                .conceptRequired(domain.getConceptRequired())
                .axen(domain.getAxen())
                .insertDate(domain.getInsertDate())
                .comments(domain.getComments())
                .build();
    }

}
