package com.u.know.loans.service.assembler;

import com.u.know.loans.controller.response.LoanOverviewResponse;
import com.u.know.loans.domain.views.ViewLoanOverview;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class LoanOverviewAssembler implements AssemblerToResponse<ViewLoanOverview, LoanOverviewResponse>{

    @Override
    public LoanOverviewResponse toResponse(ViewLoanOverview domain) {
        return LoanOverviewResponse.builder()
                .loanId(domain.getLoanId())
                .majorityPartnerId(domain.getMajorityPartnerId())
                .majorityPartner(domain.getMajorityPartner())
                .minorityPartnerId(domain.getMinorityPartnerId())
                .minorityPartner(domain.getMinorityPartner())
                .borrowerId(domain.getBorrowerId())
                .borrowerName(domain.getBorrowerName())
                .principal(domain.getPrincipal())
                .numberOfPayments(domain.getNumberOfPayments())
                .interestRate(domain.getInterestRate())
                .futureValue(domain.getFutureValue())
                .totalPayments(domain.getTotalPayments())
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
                .planVersion(domain.getPlanVersion())
                .build();
    }

    public ViewLoanOverview fromRow(Row row, RowMetadata meta) {
        return new ViewLoanOverview(
                row.get("loan_id", Integer.class),
                row.get("majority_partner_id", Integer.class),
                row.get("majority_partner", String.class),
                row.get("minority_partner_id", Integer.class),
                row.get("minority_partner", String.class),
                row.get("borrower_id", Integer.class),
                row.get("borrower_name", String.class),
                row.get("principal", BigDecimal.class),
                row.get("number_of_payments", Integer.class),
                row.get("interest_rate", BigDecimal.class),
                row.get("future_value", BigDecimal.class),
                row.get("total_payments", BigDecimal.class),
                row.get("release_date", LocalDate.class),
                row.get("first_payment_date", LocalDate.class),
                row.get("last_payment_date", LocalDate.class),
                row.get("gross_profit", BigDecimal.class),
                row.get("majority_partner_pct", BigDecimal.class),
                row.get("majority_partner_profit", BigDecimal.class),
                row.get("minority_partner_pct", BigDecimal.class),
                row.get("minority_partner_profit", BigDecimal.class),
                row.get("concept_required", String.class),
                row.get("axen", BigDecimal.class),
                row.get("insert_date", LocalDateTime.class),
                row.get("comments", String.class),
                row.get("plan_version", Integer.class)
        );
    }
}
