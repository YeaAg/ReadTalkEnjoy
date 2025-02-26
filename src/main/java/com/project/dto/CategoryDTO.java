package com.project.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Integer no;
    private String name;
    private Integer parent; // 부모 ID
    private Integer level;

    public CategoryDTO(String name, Integer parent, Integer level) {
        this.name = name;
        this.parent = parent;
        this.level = level;
    }
}


