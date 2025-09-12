package com.campusconnect.CampusConnect.service;

import com.campusconnect.CampusConnect.dto.*;
import com.campusconnect.CampusConnect.entity.Event;
import com.campusconnect.CampusConnect.entity.EventRegistration;
import com.campusconnect.CampusConnect.entity.User;
import com.campusconnect.CampusConnect.repository.EventRegistrationRepository;
import com.campusconnect.CampusConnect.repository.EventRepository;
import com.campusconnect.CampusConnect.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailEventService emailEventService;

    @Transactional
    public EventRegistrationDto registerUser(Long eventId, User user){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .ifPresent(r -> {
                    throw new IllegalStateException("User already registered for this event");
                });
        if(event.getCapacity()!=null){
            long registeredCount = registrationRepository.findByEventId(eventId).size();
            if(registeredCount >= event.getCapacity()){
                throw new IllegalStateException("Event is already full!");
            }
        }
        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);

        EventRegistration saved = registrationRepository.save(registration);

        String subject = "Registration Confirmed for " + event.getTitle();
        String body = String.format(
                "Hi %s,<br><br>You have successfully registered for <b>%s</b>.<br>" +
                        "📅 Starts at: %s<br>📍 Location: %s<br><br>Thank you!",
                user.getUsername(), event.getTitle(), event.getStartsAt(), event.getLocation()
        );
        emailEventService.sendEmail(user.getEmail(), subject, body);

        return EventRegistrationMapper.toDto(saved);
    }

    @Transactional
    public void unregisterUser(Long eventId,User user){
        EventRegistration registration = registrationRepository.findByEventIdAndUserId(eventId,user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User is not registered for this event"));
        registrationRepository.delete(registration);

        Event event = registration.getEvent();
        String subject = "Unregistration from " + event.getTitle();
        String body = String.format(
                "Hi %s,<br><br>You have successfully unregistered from <b>%s</b>.<br>" +
                        "📅 Event was scheduled at: %s<br><br>We hope to see you in future events!",
                user.getUsername(), event.getTitle(), event.getStartsAt()
        );
        emailEventService.sendEmail(user.getEmail(), subject,body);
    }

    public List<AttendeeDto> getAttendees(Long eventId){
        return registrationRepository.findByEventId(eventId)
                .stream()
                .map(registration -> AttendeeMapper.toDto(registration.getUser()))
                .toList();
    }

    public List<EventDto> getRegisteredEvents(User user){
        return registrationRepository.findByUserId(user.getId())
                .stream()
                .map(registration -> EventMapper.toDto(registration.getEvent()))
                .toList();
    }
}
