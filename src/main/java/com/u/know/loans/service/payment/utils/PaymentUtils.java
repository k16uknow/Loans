package com.u.know.loans.service.payment.utils;

import com.u.know.loans.domain.Installment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaymentUtils {

    public static LocalDate[] limitsForInstallmentSearch(LocalDate paymentDate) {
        LocalDate lowLimit;
        LocalDate highLimit;
        if(paymentDate.getDayOfMonth() >= 15 &&
                paymentDate.getDayOfMonth() < paymentDate.lengthOfMonth()){
            lowLimit = paymentDate.withDayOfMonth(15);
            highLimit = paymentDate.withDayOfMonth(paymentDate.lengthOfMonth() - 1);
        }
        else {
            if(paymentDate.getDayOfMonth() == paymentDate.lengthOfMonth()){
                lowLimit = paymentDate;
                highLimit = paymentDate.plusMonths(1).withDayOfMonth(14);
            }
            else  {
                lowLimit = paymentDate.plusMonths(-1).withDayOfMonth(paymentDate.lengthOfMonth());
                highLimit = paymentDate.withDayOfMonth(14);
            }
        }
        return new LocalDate[] {lowLimit, highLimit};
    }

    /**
     * paymentAmount = 100
     * installmentAmount = 30
     * Expected: 4 (3 installments fully, 1 partial → ceil)
     */
    public static int applicableInstallmentsForPayment(BigDecimal paymentAmount, List<Installment> installments) {
        paymentAmount = installments
                .stream()
                .map(Installment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(paymentAmount);

        var installmentAmount = installments.getFirst().getAmount();

        if (installmentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Installment amount must be greater than zero");
        }
        if (paymentAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative");
        }

        BigDecimal[] divRem = paymentAmount.divideAndRemainder(installmentAmount);
        int quotient = divRem[0].intValue();
        boolean hasRemainder = divRem[1].compareTo(BigDecimal.ZERO) > 0;
        return hasRemainder ? quotient + 1 : quotient;
    }

}
