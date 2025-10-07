package com.jobconnect_backend.converters;

import com.jobconnect_backend.dto.dto.NotificationDTO;
import com.jobconnect_backend.entities.Notification;
import com.jobconnect_backend.populators.NotificationPopulator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class NotificationConverter {
    private final NotificationPopulator notificationPopulator;

    public NotificationDTO convertToNotificationDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        notificationPopulator.populate(notification, dto);
        return dto;
    }
}
