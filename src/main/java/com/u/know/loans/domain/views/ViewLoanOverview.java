package com.u.know.loans.domain.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("loan_overview")
@Immutable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewLoanOverview {

    @Id
    private Integer loanId;
    private Integer majorityPartnerId;
    private String majorityPartner;
    private Integer minorityPartnerId;
    private String minorityPartner;
    private Integer borrowerId;
    private String borrowerName;
    private BigDecimal principal;
    private Integer numberOfPayments;
    private BigDecimal interestRate;
    private BigDecimal futureValue;
    private BigDecimal totalPayments;
    private LocalDate releaseDate;
    private LocalDate firstPaymentDate;
    private LocalDate lastPaymentDate;
    private BigDecimal grossProfit;
    private BigDecimal majorityPartnerPct;
    private BigDecimal majorityPartnerProfit;
    private BigDecimal minorityPartnerPct;
    private BigDecimal minorityPartnerProfit;
    private String conceptRequired;
    private BigDecimal axen;
    private LocalDateTime insertDate;
    private String comments;
    private Integer planVersion;

}
