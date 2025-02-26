package com.project.service;

import com.project.dto.NotificationDTO;
import com.project.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired private NotificationMapper notificationMapper;
    public void createNotification(NotificationDTO notification) {
        notificationMapper.createNotification(notification);
    }

    public void updateNotificationStatus(Integer id, String status) {
        notificationMapper.updateNotificationStatus(id, status);
    }

    public List<NotificationDTO> getNotificationsByUserId(String userId) {
        return notificationMapper.getNotificationByUserId(userId);
    }

    public void createLoanReminderNotification(String userId, String bookTitle, Integer dayLeft) {
        String message = "대출하신 책 '" + bookTitle + "'의 반납 기한이 " + dayLeft + "일 남았습니다.";
        notificationMapper.createLoanReminderNotification(userId, message, "대출 기한 경고");
    }

    public void createPurchaseNotification(String userId, String bookTitle) {
        String message = "책 '" + bookTitle + "' 구매가 완료되었습니다.";
        notificationMapper.createPurchaseNotification(userId, message, "책 구매 완료");
    }

    public void createDiscussionNotification(String userId, String discussionTitle, String commentAuthor) {
        String message = "토론 '" + discussionTitle + "'에" + commentAuthor + "님이 댓글을 남겼습니다.";
        notificationMapper.createDiscussionNotification(userId, message, "토론 알림");
    }

    public void createSignUpNotification(String userId) {
        String message = "회원가입이 완료되었습니다. 환영합니다!";
        notificationMapper.createSignUpNotification(userId, message, "회원가입 완료");
    }
}
