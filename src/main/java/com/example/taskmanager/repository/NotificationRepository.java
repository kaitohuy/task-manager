package com.example.taskmanager.repository;

import com.example.taskmanager.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientUsernameOrderByCreatedAtDesc(String username, Pageable pageable);
    long countByRecipientUsernameAndIsReadFalse(String username);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient.username = :username AND n.isRead = false")
    void markAllAsReadByUsername(String username);

    void deleteByRecipientUsername(String username);
}