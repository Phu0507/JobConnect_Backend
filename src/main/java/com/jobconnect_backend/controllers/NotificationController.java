package com.jobconnect_backend.controllers;

import com.jobconnect_backend.dto.dto.NotificationDTO;
import com.jobconnect_backend.services.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationService notificationServiceImpl;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(notificationServiceImpl.getNotificationsByUserId(userId));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Integer userId) {
        return ResponseEntity.ok(notificationServiceImpl.countUnreadNotifications(userId));
    }
}
