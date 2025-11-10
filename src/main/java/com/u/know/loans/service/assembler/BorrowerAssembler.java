package com.u.know.loans.service.assembler;

import com.u.know.loans.controller.request.BorrowerRequest;
import com.u.know.loans.controller.response.BorrowerResponse;
import com.u.know.loans.dto.Borrower;
import org.springframework.stereotype.Component;

@Component
public class BorrowerAssembler implements Assembler<Borrower, BorrowerRequest, BorrowerResponse>{

    @Override
    public Borrower fromRequest(BorrowerRequest request) {
        return Borrower.builder()
                .firstName(request.firstName())
                .paternalLast(request.paternalLast())
                .maternalLast(request.maternalLast())
                .phone(request.phone())
                .address(request.address())
                .occupation(request.occupation())
                .workplace(request.workplace())
                .status(request.status())
                .rating(request.rating())
                .build();
    }

    @Override
    public BorrowerResponse toResponse(Borrower domain) {
        return BorrowerResponse.builder()
                .id(domain.getId())
                .firstName(domain.getFirstName())
                .paternalLast(domain.getPaternalLast())
                .maternalLast(domain.getMaternalLast())
                .phone(domain.getPhone())
                .address(domain.getAddress())
                .occupation(domain.getOccupation())
                .workplace(domain.getWorkplace())
                .status(domain.getStatus())
                .rating(domain.getRating())
                .build();
    }

    public Borrower updateFromRequest(Borrower borrower, BorrowerRequest request) {
        borrower.setFirstName(request.firstName());
        borrower.setPaternalLast(request.paternalLast());
        borrower.setMaternalLast(request.maternalLast());
        borrower.setPhone(request.phone());
        borrower.setAddress(request.address());
        borrower.setOccupation(request.occupation());
        borrower.setWorkplace(request.workplace());
        borrower.setStatus(request.status());
        borrower.setRating(request.rating());
        return borrower;
    }
}
