package com.u.know.loans.service.utils;

import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.dto.Loan;
import com.u.know.loans.exception.BadRequestException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class LoanHeaderGenerator {

    public static void fillLoanHeader(Loan loan, BorrowerResponse borrower, Integer majPartnerId, Integer minPartnerId) {
        calculateConceptRequired(loan, borrower);
        loan.setBorrowerId(borrower.id());
        loan.setMajorityPartnerId(majPartnerId);
        loan.setMinorityPartnerId(minPartnerId);
        loan.setPlanVersion(1);
        loan.setInsertDate(LocalDateTime.now());
    }

    private static void calculateConceptRequired(Loan loan, BorrowerResponse borrower) {
        loan.setConceptRequired("P" + loan.getId() + " de " + borrower.firstName() + " " + borrower.paternalLast());
        calculatePaymentDates(loan);
    }

    private static void calculatePaymentDates(Loan loan) {
        if(loan.getNumberOfPayments() % 2 != 0) {
            throw new BadRequestException("Number of payments must be even");
        }
        loan.setFirstPaymentDate(loan.getReleaseDate().getDayOfMonth() == 15 ?
                        loan.getReleaseDate().withDayOfMonth(loan.getReleaseDate().lengthOfMonth()) :
                loan.getReleaseDate().withDayOfMonth(15));

        loan.setLastPaymentDate(loan.getReleaseDate().plusMonths(loan.getNumberOfPayments() / 2));
        calculateFutureValue(loan);
    }

    private static void calculateFutureValue(Loan loan) {
        loan.setFutureValue(loan.getPrincipal()
                .multiply((BigDecimal.ONE)
                        .add(loan.getInterestRate()
                                .divide(BigDecimal
                                        .valueOf(100), 4, RoundingMode.HALF_UP)
                        )
                ));
        calculateGrossProfit(loan);
    }

    private static void calculateGrossProfit(Loan loan) {
        loan.setGrossProfit(loan.getFutureValue().subtract(loan.getPrincipal()));
        calculatePartnersProfit(loan);
    }

    private static void calculatePartnersProfit(Loan loan) {
        if(loan.getMinorityPartnerPct().compareTo(BigDecimal.valueOf(49.9999)) > 0) {
          throw new BadRequestException("Minority Partner can't receive more percentage");
        }
        loan.setMajorityPartnerPct(BigDecimal.valueOf(100).subtract(loan.getMinorityPartnerPct()));
        loan.setMajorityPartnerProfit(loan.getGrossProfit()
                        .multiply(loan.getMajorityPartnerPct()
                                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                        ));
        loan.setMinorityPartnerProfit(loan.getGrossProfit().subtract(loan.getMajorityPartnerProfit()));
        calculateAxenProfit(loan);
    }

    private static void calculateAxenProfit(Loan loan) {
        double monthlyRate = 0.03;
        double months = (double) loan.getNumberOfPayments() /2;
        loan.setAxen((loan.getPrincipal().multiply(BigDecimal.valueOf(Math.pow(1 + monthlyRate, months)))).subtract(loan.getPrincipal()));
    }

}
