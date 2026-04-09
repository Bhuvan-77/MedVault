package com.medvault.medvault.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.medvault.medvault.entity.Notification;
import com.medvault.medvault.entity.User;
import com.medvault.medvault.enums.Role;
import com.medvault.medvault.repository.NotificationRepository;
import com.medvault.medvault.repository.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getNotificationsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    public long getUnreadCountByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    public Map<String, Object> markAsRead(String email, Long notificationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot update this notification");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        return toMap(notification);
    }

    public Map<String, Object> markAllAsRead(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        int updated = 0;
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.setRead(true);
                updated++;
            }
        }
        if (updated > 0) {
            notificationRepository.saveAll(notifications);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("updated", updated);
        return response;
    }

    public void notifyUser(User recipient, String type, String title, String message, String actionUrl) {
        if (recipient == null) {
            return;
        }

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setActionUrl(actionUrl);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    public void notifyRole(Role role, String type, String title, String message, String actionUrl) {
        List<User> users = userRepository.findByRole(role);
        for (User user : users) {
            notifyUser(user, type, title, message, actionUrl);
        }
    }

    private Map<String, Object> toMap(Notification notification) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", notification.getId());
        map.put("type", notification.getType());
        map.put("title", notification.getTitle());
        map.put("message", notification.getMessage());
        map.put("actionUrl", notification.getActionUrl());
        map.put("read", notification.isRead());
        map.put("createdAt", notification.getCreatedAt());
        return map;
    }
}
