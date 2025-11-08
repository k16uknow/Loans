package com.u.know.loans.service.assembler;
import com.u.know.loans.dto.Partner;
import com.u.know.loans.controller.request.PartnerRequest;
import com.u.know.loans.controller.response.PartnerResponse;
import org.springframework.stereotype.Component;

@Component
public class PartnerAssembler implements Assembler<Partner, PartnerRequest, PartnerResponse> {

    @Override
    public Partner fromRequest(PartnerRequest request) {
        return new Partner(request.name());
    }

    @Override
    public PartnerResponse toResponse(Partner domain) {
        return new PartnerResponse(domain.getId(), domain.getName());
    }

}
