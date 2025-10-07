package com.jobconnect_backend.populators;

import com.jobconnect_backend.dto.dto.NotificationDTO;
import com.jobconnect_backend.entities.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationPopulator {
    public void populate(Notification source, NotificationDTO target) {
        target.setNotiId(source.getNotificationId());
        target.setJobTitle(source.getApplication().getJob().getTitle());
        target.setContent(source.getContent());
        target.setIsRead(source.getIsRead());
        target.setCreatedAt(source.getCreatedAt());
        target.setUserId(source.getUser().getUserId());
        target.setApplicationId(source.getApplication().getApplicationId());
    }
}
