package com.jobconnect_backend.services;

import com.jobconnect_backend.dto.dto.NotificationDTO;
import com.jobconnect_backend.dto.request.CreateNotiRequest;

import java.util.List;

public interface INotificationService {
    NotificationDTO createNoti(CreateNotiRequest request);
    List<NotificationDTO> getNotificationsByUserId(Integer userId);
    Long countUnreadNotifications(Integer userId);
}
