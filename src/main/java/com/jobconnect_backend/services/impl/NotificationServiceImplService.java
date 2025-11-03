package com.jobconnect_backend.services.impl;

import com.jobconnect_backend.converters.NotificationConverter;
import com.jobconnect_backend.dto.dto.NotificationDTO;
import com.jobconnect_backend.dto.request.CreateNotiRequest;
import com.jobconnect_backend.entities.Application;
import com.jobconnect_backend.entities.Notification;
import com.jobconnect_backend.entities.User;
import com.jobconnect_backend.exception.BadRequestException;
import com.jobconnect_backend.repositories.ApplicationRepository;
import com.jobconnect_backend.repositories.NotificationRepository;
import com.jobconnect_backend.repositories.UserRepository;
import com.jobconnect_backend.services.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImplService implements INotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationConverter notificationConverter;

    @Override
    public NotificationDTO createNoti(CreateNotiRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        Optional<Application> application = applicationRepository.findById(request.getApplicationId());
        Notification notification = Notification.builder()
                .user(user.orElseThrow(() -> new BadRequestException("User not found")))
                .application(application.orElseThrow(() -> new BadRequestException("Application not found")))
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        return notificationConverter.convertToNotificationDTO(notification);
    }

    @Override
    public List<NotificationDTO> getNotificationsByUserId(Integer userId) {
        List<Notification> notification = notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        return notification.stream()
                .map(notificationConverter::convertToNotificationDTO)
                .collect(Collectors.toList());
    }
}
