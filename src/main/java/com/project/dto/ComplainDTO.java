package com.project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class ComplainDTO {
    private Integer no;
    private String title;
    private String contents;
    private String userId;
    private LocalDateTime uploadedAt;
    private String answer;

    private List<UserDTO> user;
}
