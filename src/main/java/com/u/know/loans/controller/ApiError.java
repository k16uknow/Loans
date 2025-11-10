package com.u.know.loans.controller;

public record ApiError(
        String code,
        String message
) {}
