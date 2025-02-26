package com.project.mapper;

import com.project.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void createNotification(NotificationDTO notification);
    void updateNotificationStatus(Integer id, String status);
    List<NotificationDTO> getNotificationByUserId(String userId);
    void createLoanReminderNotification(String userId, String message, String type);
    void createPointEarnedNotification(String userId, String message, String type);
    void createPurchaseNotification(String userId, String message, String type);
    void createDiscussionNotification(String userId, String message, String type);
    void createSignUpNotification(String userId, String message, String type);
}
