package com.u.know.loans.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("borrower")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Borrower {

    @Id
    private Integer id;
    private String firstName;
    private String paternalLast;
    private String maternalLast;
    private String phone;
    private String address;
    private String occupation;
    private String workplace;
    private String status;
    private Integer rating;

}
