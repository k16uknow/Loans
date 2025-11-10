package com.u.know.loans.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("loan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    private Integer id;
    private Integer majorityPartnerId;
    private Integer minorityPartnerId;
    private Integer borrowerId;
    private BigDecimal principal;
    private Integer numberOfPayments;
    private BigDecimal interestRate;
    private BigDecimal futureValue;
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
    private LocalDate insertDate;
    private String comments;

}
