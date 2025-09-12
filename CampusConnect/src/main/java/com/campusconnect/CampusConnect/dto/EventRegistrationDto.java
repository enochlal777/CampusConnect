package com.campusconnect.CampusConnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationDto {
    private Long id;
    private AttendeeDto attendee;
    private Long eventId;
    private Instant registeredAt;
}
