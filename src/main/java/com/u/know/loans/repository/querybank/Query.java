package com.u.know.loans.repository.querybank;

import lombok.Getter;

@Getter
public enum Query {

    GET_TOTAL_LOAN_COUNT ("SELECT COUNT(*) FROM loan_overview"),

    GET_LOAN_OVERVIEWS ("SELECT loan_id, majority_partner_id, majority_partner, minority_partner_id, minority_partner, borrower_id, borrower_name, principal, number_of_payments, interest_rate, future_value, total_payments, release_date, first_payment_date, last_payment_date, gross_profit, majority_partner_pct, majority_partner_profit, minority_partner_pct, minority_partner_profit, concept_required, axen, insert_date, comments, plan_version FROM loan_overview");

    private final String query;

    Query(String query) {
        this.query = query;
    }
}
