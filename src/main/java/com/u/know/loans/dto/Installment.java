package com.u.know.loans.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("installment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Installment {

    @Id
    private Integer id;
    private Integer loanId;
    private Integer planVersion;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal totalPayments;
    private BigDecimal statementBalance;
    private String status;
}
