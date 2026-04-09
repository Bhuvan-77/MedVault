package com.medvault.medvault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medvault.medvault.entity.Notification;
import com.medvault.medvault.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    long countByRecipientAndReadFalse(User recipient);
}
