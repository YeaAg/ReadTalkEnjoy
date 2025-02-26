package com.project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class LoanDTO {
    private Integer id;
    private UserDTO user;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
    private String status;

    private String userId;
    private String bookIsbn;

    private Integer originalPrice;
    private Integer discountPrice;
    private Integer finalPrice;

    private String impUid;
}
