package com.campusconnect.CampusConnect.dto;

import com.campusconnect.CampusConnect.entity.Notification;

public class NotificationMapper {
    public static NotificationDto toDto(Notification notification){
        return NotificationDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
