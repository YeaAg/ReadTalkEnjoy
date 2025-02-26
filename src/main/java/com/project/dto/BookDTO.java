package com.project.dto;

import com.project.dto.CategoryDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class BookDTO {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String publicationDate;
    private Integer price;
    private Integer copiesAvailable;
    private String detail;
    private CategoryDTO category;
    private byte[] image;
    private String base64Image;
    // 등록일자
    private LocalDateTime createdAt;
    private Integer itemId;
    private Integer pageCount;

    private List<BookImageDTO> bookImages;

    public void setImage(byte[] image) {
        this.image = image;
        if (image != null) {
            this.base64Image = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(image);
        } else {
            this.base64Image = null;
        }
    }
}
