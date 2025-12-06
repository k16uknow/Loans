package com.u.know.loans.controller.response;

public record LoginResponse(
        String token,
        String refreshToken) { }
