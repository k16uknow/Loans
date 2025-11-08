package com.u.know.loans.domain;

import com.u.know.loans.controller.request.PartnerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("partner")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Partner {

    @Id
    private Integer id;

    private String name;

    public Partner (PartnerRequest request) {
        this.name = request.getName();
    }

}
