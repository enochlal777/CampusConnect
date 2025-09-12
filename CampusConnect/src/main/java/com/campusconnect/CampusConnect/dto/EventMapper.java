package com.campusconnect.CampusConnect.dto;

import com.campusconnect.CampusConnect.entity.Event;

public class EventMapper {
    public static EventDto toDto(Event event) {
        Integer capacity = event.getCapacity();
        Integer registeredCount = (event.getRegistrations() !=null) ? event.getRegistrations().size() : 0;

        Integer remainingSeats = (capacity !=null)?Math.max(capacity - registeredCount,0):null;
        return new EventDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartsAt(),
                event.getEndsAt(),
                event.getCategory(),
                event.isPublished(),
                event.getStatus().name(),
                event.getOrganizer().getUsername(),
                capacity,
                remainingSeats
        );
    }
}
