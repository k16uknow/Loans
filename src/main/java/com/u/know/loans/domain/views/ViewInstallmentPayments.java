package com.u.know.loans.domain.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("installment_payments")
@Immutable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewInstallmentPayments {

    @Id
    private Integer id;
    private Integer loanId;
    private LocalDate dueDate;
    private Integer installmentNo;
    private BigDecimal amount;
    private BigDecimal paymentAmount;

}
