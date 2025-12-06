package com.u.know.loans.service.assembler;

public interface Assembler<D, RQ, RP> extends AssemblerToResponse<D, RP> {

    D fromRequest(RQ request);

}
