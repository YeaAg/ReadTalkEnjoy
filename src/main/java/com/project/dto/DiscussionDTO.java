package com.project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Log4j2
public class DiscussionDTO {
    private Integer id;
    private String bookTitle;
    private String topic;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String userId;
    private String bookIsbn;

    private byte[] bookImage;

    private String base64Image;

    private List<UserDTO> user;
    private List<BookDTO> book;
    private List<DiscussionCommentDTO> comment;

    private String recentComment;

    public void setBookImage(byte[] bookImage) {
        this.bookImage = bookImage;
        log.error(bookImage);
        try {
            if (bookImage != null) {
                this.base64Image = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(bookImage);
            } else {
                this.base64Image = null;
            }
        } catch (Exception e) {
            log.error("Failed to encode image to Base64", e);
            this.base64Image = null;
        }
    }

}
