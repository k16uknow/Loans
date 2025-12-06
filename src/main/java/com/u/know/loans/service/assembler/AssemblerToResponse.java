package com.u.know.loans.service.assembler;

public interface AssemblerToResponse<D, RP>{

    RP toResponse(D domain);

}
