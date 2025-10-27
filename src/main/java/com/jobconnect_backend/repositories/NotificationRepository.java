package com.jobconnect_backend.repositories;

import com.jobconnect_backend.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserUserIdOrderByCreatedAtDesc(Integer userId);
    Long countByUserUserIdAndIsReadFalse(Integer userId);
    void deleteByUserUserId(Integer userId);
}
