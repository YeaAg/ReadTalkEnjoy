package com.project.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookImageDTO {
    private Integer no;
    private String bookIsbn; // Lombok에서 getter를 자동 생성
    private String originalFilename;
    @ToString.Exclude
    private byte[] data;
}

