package com.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // 🔥 JSON 변환 시 알 수 없는 필드 무시
public class DiscussionCommentDTO {
    private Integer id;
    private String content;
    private Integer like;
    private Integer unlike;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String userId;
    private Integer discussionId;
    private String votedUserIds;

    private List<UserDTO> user;
    private List<DiscussionDTO> discussion;
}
