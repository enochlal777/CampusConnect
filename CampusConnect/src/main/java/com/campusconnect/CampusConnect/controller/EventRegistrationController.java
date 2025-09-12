package com.campusconnect.CampusConnect.controller;

import com.campusconnect.CampusConnect.dto.AttendeeDto;
import com.campusconnect.CampusConnect.dto.EventDto;
import com.campusconnect.CampusConnect.dto.EventRegistrationDto;
import com.campusconnect.CampusConnect.entity.User;
import com.campusconnect.CampusConnect.repository.UserRepository;
import com.campusconnect.CampusConnect.service.EventRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class EventRegistrationController {

    @Autowired
    private EventRegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails){
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<EventRegistrationDto>  registerForEvent(@PathVariable Long eventId, @AuthenticationPrincipal UserDetails userDetails){
        User currentUser = getCurrentUser(userDetails);
        EventRegistrationDto registration = registrationService.registerUser(eventId,currentUser);
        return ResponseEntity.ok(registration);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> unregisterFromEvent(@PathVariable Long eventId,@AuthenticationPrincipal UserDetails userDetails){
        User currentUser = getCurrentUser(userDetails);
        registrationService.unregisterUser(eventId,currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/attendees")
    public ResponseEntity<List<AttendeeDto>> getAttendees(@PathVariable Long eventId){
        return ResponseEntity.ok(registrationService.getAttendees(eventId));
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<EventDto>> getMyRegisteredEvents(@AuthenticationPrincipal UserDetails userDetails){
        User currentUser = getCurrentUser(userDetails);
        List<EventDto> events = registrationService.getRegisteredEvents(currentUser);
        return ResponseEntity.ok(events);
    }
}
