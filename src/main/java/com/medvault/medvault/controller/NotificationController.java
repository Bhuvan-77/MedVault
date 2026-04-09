package com.medvault.medvault.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medvault.medvault.service.NotificationService;

@CrossOrigin(origins = "http://localhost:3001")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getNotifications(@PathVariable String email) {
        return ResponseEntity.ok(notificationService.getNotificationsByEmail(email));
    }

    @GetMapping("/{email}/unread-count")
    public ResponseEntity<?> getUnreadCount(@PathVariable String email) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCountByEmail(email)));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId,
                                        @RequestParam String email) {
        return ResponseEntity.ok(notificationService.markAsRead(email, notificationId));
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestParam String email) {
        return ResponseEntity.ok(notificationService.markAllAsRead(email));
    }
}
