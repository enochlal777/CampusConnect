package com.campusconnect.CampusConnect.dto;

import com.campusconnect.CampusConnect.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String message;
    private NotificationType type;
    private boolean read;
    private Instant createdAt;
}
