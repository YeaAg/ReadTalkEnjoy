package com.project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CartDTO {
    private Integer no;
    private UserDTO user;
    private BookDTO book;
}
