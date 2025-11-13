package com.u.know.loans.service.utils;

import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.dto.Loan;
import com.u.know.loans.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class LoanHeaderGeneratorTest {

    private Loan loan;

    private BorrowerResponse borrowerResponse;

    @BeforeEach
    void setup () {
        loan = Loan.builder()
                .borrowerId(1)
                .majorityPartnerId(1)
                .minorityPartnerId(2)
                .minorityPartnerPct(BigDecimal.valueOf(30))
                .principal(BigDecimal.valueOf(10_000))
                .numberOfPayments(10)
                .interestRate(BigDecimal.valueOf(48))
                .releaseDate(LocalDate.of(2025, 11, 15))
                .minorityPartnerPct(BigDecimal.valueOf(30))
                .comments("test comments")
                .build();

        borrowerResponse = BorrowerResponse
                .builder()
                .id(1)
                .firstName("Test")
                .paternalLast("Test")
                .build();
    }

    @Test
    void fillLoanHeader() {

        LoanHeaderGenerator.fillLoanHeader(loan, borrowerResponse, 1, 2);

        assertEquals(1, loan.getPlanVersion());
        assertEquals(LocalDate.of(2025, 11, 30), loan.getFirstPaymentDate());
        assertEquals(LocalDate.of(2026, 4, 15), loan.getLastPaymentDate());
        assertEquals(BigDecimal.valueOf(70), loan.getMajorityPartnerPct());
        Assertions.assertThat(loan.getGrossProfit()).isEqualByComparingTo("4800");
        Assertions.assertThat(loan.getMajorityPartnerProfit()).isEqualByComparingTo("3360");
        Assertions.assertThat(loan.getMinorityPartnerProfit()).isEqualByComparingTo("1440");
        Assertions.assertThat(loan.getAxen()).isEqualByComparingTo("1592.740743");
    }

    @Test
    void fillLoanHeader_BadRequest_ByMinorityPartnerPct() {
        loan.setMinorityPartnerPct(BigDecimal.valueOf(50.01));
        assertThrows(BadRequestException.class, () -> LoanHeaderGenerator.fillLoanHeader(loan, borrowerResponse,1,2));
    }

    @Test
    void fillLoanHeader_BadRequest_ByOddNumberOfPayments() {
        loan.setNumberOfPayments(11);
        assertThrows(BadRequestException.class, () -> LoanHeaderGenerator.fillLoanHeader(loan, borrowerResponse,1,2));
    }
}