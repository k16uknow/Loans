package com.u.know.loans.controller.request;

import lombok.Builder;

@Builder
public record BorrowerRequest (
        String firstName,
        String paternalLast,
        String maternalLast,
        String phone,
        String address,
        String occupation,
        String workplace,
        String status,
        Integer rating) { }
