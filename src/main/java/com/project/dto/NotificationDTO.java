package com.project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class NotificationDTO {
    private Integer id;
    private String message;
    private String status;

    private String userId;
    private String type;

    private List<UserDTO> user;

}
