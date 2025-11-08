package com.u.know.loans.service.assembler;

public interface Assembler<D, RQ, RP> {
    D fromRequest(RQ request);

    RP toResponse(D domain);
}
