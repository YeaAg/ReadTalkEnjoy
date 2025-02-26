package com.project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class ReviewDTO {
    private String userId;  // 리뷰를 등록한 사람
    private String bookIsbn;  // 리뷰를 등록한 상품명
    private String content;    // 리뷰 내용
    private Integer like;
    private Integer rate;   // 별점
    private LocalDateTime updatedAt;

    private byte[] userImage;
    private String base64Image;


    private List<UserDTO> user;
    private List<BookDTO> book;
    public void setReviewImage(byte[] userImage) {
        this.userImage = userImage;
        if (userImage != null) {
            this.base64Image = "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(userImage);
        } else {
            this.base64Image = null; // 기본 이미지 설정
        }
    }


}
