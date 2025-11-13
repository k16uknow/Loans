package com.u.know.loans.service.utils;


import com.u.know.loans.dto.Installment;
import com.u.know.loans.dto.Loan;
import com.u.know.loans.service.enums.InstallmentStatusEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class InstallmentGenerator {

    public static List<Installment> generateInstallmentsFirstPlanVersion(Loan loan) {

        BigDecimal amount = loan.getFutureValue()
                .divide(BigDecimal.valueOf(loan.getNumberOfPayments()), 4, RoundingMode.HALF_UP);


        return IntStream.range(0, loan.getNumberOfPayments()).mapToObj(
                paymentNo -> {
                    var totalPayments = amount.multiply(BigDecimal.valueOf(paymentNo));
                    var statementBalance = loan.getFutureValue().subtract(totalPayments);
                    return Installment
                            .builder()
                            .loanId(loan.getId())
                            .planVersion(1)
                            .installmentNo(paymentNo + 1)
                            .dueDate(calculateNextDueDate(loan.getFirstPaymentDate(), paymentNo))
                            .amount(amount)
                            .totalPayments(totalPayments)
                            .statementBalance(statementBalance)
                            .status(InstallmentStatusEnum.ACTIVE.getValue())
                            .build();
                }
        ).toList();
    }

    private static LocalDate calculateNextDueDate(LocalDate initialDueDate, int installmentIndex) {
        boolean startsOn15 = initialDueDate.getDayOfMonth() == 15;

        if (startsOn15) {
            // 0 -> 15 (month +0)
            // 1 -> EOM-1 (month +0)
            // 2 -> 15 (month +1)
            // 3 -> EOM-1 (month +1)
            int monthOffset = installmentIndex / 2;
            LocalDate baseMonth = initialDueDate.plusMonths(monthOffset);

            if (installmentIndex % 2 == 0) {
                // even -> 15th
                return baseMonth.withDayOfMonth(15);
            } else {
                // odd -> EOM-1
                return baseMonth.withDayOfMonth(baseMonth.lengthOfMonth());
            }
        } else {
            // starts on EOM-1
            // 0 -> EOM-1 (month +0)
            // 1 -> 15     (month +1)
            // 2 -> EOM-1  (month +1)
            // 3 -> 15     (month +2)
            if (installmentIndex % 2 == 0) {
                // even -> EOM-1 of (month + installmentIndex/2)
                int monthOffset = installmentIndex / 2;
                LocalDate baseMonth = initialDueDate.plusMonths(monthOffset);
                return baseMonth.withDayOfMonth(baseMonth.lengthOfMonth());
            } else {
                // odd -> 15 of (month + (installmentIndex + 1)/2)
                int monthOffset = (installmentIndex + 1) / 2;
                LocalDate baseMonth = initialDueDate.plusMonths(monthOffset);
                return baseMonth.withDayOfMonth(15);
            }
        }
    }
}
