package com.campusconnect.CampusConnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Instant startsAt;
    private Instant endsAt;
    private String category;
    private boolean published;
    private String status;
    private String organizerUsername;
    private Integer capacity;
    private Integer remainingSeats;
}
