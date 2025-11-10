package com.u.know.loans.controller.response;

import lombok.Builder;

@Builder
public record BorrowerResponse(
    Integer id,
    String firstName,
    String paternalLast,
    String maternalLast,
    String phone,
    String address,
    String occupation,
    String workplace,
    String status,
    Integer rating) { }
