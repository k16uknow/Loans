package com.u.know.loans.controller.response.wrapper;

public record ApiError(
        String code,
        String message
) {}
