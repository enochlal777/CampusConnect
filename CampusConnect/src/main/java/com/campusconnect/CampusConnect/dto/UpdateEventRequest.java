package com.campusconnect.CampusConnect.dto;

import com.campusconnect.CampusConnect.entity.EventStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class UpdateEventRequest {
    private String title;
    private String description;
    private String location;
    private Instant startsAt;
    private Instant endsAt;
    private String category;
    private Integer capacity;
    private boolean published;
    private EventStatus status;
}
