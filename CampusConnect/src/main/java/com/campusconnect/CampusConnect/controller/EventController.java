package com.campusconnect.CampusConnect.controller;

import com.campusconnect.CampusConnect.dto.CreateEventRequest;
import com.campusconnect.CampusConnect.dto.UpdateEventRequest;
import com.campusconnect.CampusConnect.dto.EventDto;
import com.campusconnect.CampusConnect.entity.EventStatus;
import com.campusconnect.CampusConnect.entity.User;
import com.campusconnect.CampusConnect.repository.UserRepository;
import com.campusconnect.CampusConnect.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody CreateEventRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(eventService.createEvent(request, currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(eventService.getEvent(id, currentUser));
    }

    @GetMapping("/published")
    public ResponseEntity<List<EventDto>> getPublishedEvents() {
        return ResponseEntity.ok(eventService.getPublishedEvents());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDto>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/past")
    public ResponseEntity<List<EventDto>> getPastEvents() {
        return ResponseEntity.ok(eventService.getPastEvents());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id,
                                                @RequestBody UpdateEventRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(eventService.updateEvent(id, request, currentUser));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<EventDto> publishEvent(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(eventService.publishEvent(id, currentUser));
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<EventDto> unpublishEvent(@PathVariable Long id,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(eventService.unpublishEvent(id, currentUser));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<EventDto> changeStatus(@PathVariable Long id,
                                                 @RequestParam EventStatus status,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(eventService.changeStatus(id, status, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        eventService.deleteEvent(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvents(@RequestParam String keyword){
        return ResponseEntity.ok(eventService.searchEvents(keyword));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EventDto>> filterEvents(@RequestParam(required = false) String category, @RequestParam(required = false) String location, @RequestParam(required = false) String keyword, @RequestParam(required = false) Instant startDate, @RequestParam(required = false) Instant endDate, @RequestParam(required = false) String organizerUsername){
        return ResponseEntity.ok(eventService.filterEvent(category, location, keyword, startDate, endDate, organizerUsername));
    }
}
