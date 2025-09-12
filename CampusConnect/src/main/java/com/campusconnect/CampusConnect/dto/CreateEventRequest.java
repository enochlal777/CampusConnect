package com.campusconnect.CampusConnect.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CreateEventRequest {
    private String title;
    private String description;
    private String location;
    private Instant startsAt;
    private Instant endsAt;
    private String category;
    private Integer capacity;
}
