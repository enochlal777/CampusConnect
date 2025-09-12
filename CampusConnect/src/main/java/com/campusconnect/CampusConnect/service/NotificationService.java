package com.campusconnect.CampusConnect.service;

import com.campusconnect.CampusConnect.dto.NotificationDto;
import com.campusconnect.CampusConnect.dto.NotificationMapper;
import com.campusconnect.CampusConnect.entity.Notification;
import com.campusconnect.CampusConnect.entity.NotificationType;
import com.campusconnect.CampusConnect.entity.User;
import com.campusconnect.CampusConnect.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendNotification(User user, String message, NotificationType type){
        Notification notification = new Notification(message,type,user);
        notificationRepository.save(notification);
    }

    public List<NotificationDto> getUserNotifications(User user){
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId,User user){
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if(!notification.getUser().getId().equals(user.getId())){
            throw new SecurityException("You cannot modify someone else's notification");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
