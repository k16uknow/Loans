package com.u.know.loans.repository.querybank;

import java.util.Map;

public record QueryAndFiltersAndClauses(
        String query,
        Map<Clause, Object> filters) { }
