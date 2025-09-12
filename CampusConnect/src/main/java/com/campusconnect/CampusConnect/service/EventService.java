package com.campusconnect.CampusConnect.service;

import com.campusconnect.CampusConnect.dto.CreateEventRequest;
import com.campusconnect.CampusConnect.dto.EventDto;
import com.campusconnect.CampusConnect.dto.EventMapper;
import com.campusconnect.CampusConnect.dto.UpdateEventRequest;
import com.campusconnect.CampusConnect.entity.*;
import com.campusconnect.CampusConnect.repository.EventRegistrationRepository;
import com.campusconnect.CampusConnect.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private EmailEventService emailEventService;


    public List<EventDto> filterEvent(String category,String location,String keyword,Instant startDate,Instant endDate,String organizerUsername){
        List<Event> results;

        if(keyword !=null && !keyword.isEmpty()){
            results = eventRepository.searchPublishedEventsByKeyword(keyword);
        }
        else if(category!=null){
            results = eventRepository.findByPublishedTrueAndCategoryIgnoreCase(category);
        }
        else if(location!=null){
            results = eventRepository.findByLocationIgnoreCase(location);
        }
        else if(startDate!=null && endDate!=null){
            results = eventRepository.findByStartsAtBetween(startDate,endDate);
        }
        else if(organizerUsername!=null){
            results = eventRepository.findByOrganizer_UsernameIgnoreCase(organizerUsername);
        }
        else{
            results = eventRepository.findByPublishedTrue();
        }

        return results.stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EventDto> searchEvents(String keyword){
        return eventRepository.searchPublishedEventsByKeyword(keyword)
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventDto createEvent(CreateEventRequest request, User currentUser) {
        if (!hasRole(currentUser, "ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can create events.");
        }

        validateEvent(request);

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartsAt(request.getStartsAt());
        event.setEndsAt(request.getEndsAt());
        event.setCategory(request.getCategory());
        event.setCapacity(request.getCapacity());

        event.setStatus(EventStatus.SCHEDULED);
        event.setPublished(false);
        event.setOrganizer(currentUser);

        return EventMapper.toDto(eventRepository.save(event));
    }

    public EventDto getEvent(Long id, User currentUser) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("event not found with id: " + id));
        if (!hasRole(currentUser, "ROLE_ADMIN") && !event.isPublished()) {
            throw new AccessDeniedException("You are not allowed to view the unpublished event.");
        }
        return EventMapper.toDto(event);
    }

    public List<EventDto> getPublishedEvents() {
        return eventRepository.findByPublishedTrue()
                .stream().map(EventMapper::toDto).collect(Collectors.toList());
    }

    public List<EventDto> getUpcomingEvents() {
        return eventRepository.findByStartsAtAfter(Instant.now())
                .stream().map(EventMapper::toDto).collect(Collectors.toList());
    }

    public List<EventDto> getPastEvents() {
        return eventRepository.findByEndsAtBefore(Instant.now())
                .stream().map(EventMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public EventDto updateEvent(Long id, UpdateEventRequest request, User currentUser) {
        Event existing = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));

        if (!hasRole(currentUser, "ROLE_ADMIN") &&
                !existing.getOrganizer().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot update this event.");
        }

        validateEvent(request);

        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setLocation(request.getLocation());
        existing.setStartsAt(request.getStartsAt());
        existing.setEndsAt(request.getEndsAt());
        existing.setCategory(request.getCategory());
        existing.setCapacity(request.getCapacity());

        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        existing.setPublished(request.isPublished());

        return EventMapper.toDto(eventRepository.save(existing));
    }

    @Transactional
    public EventDto publishEvent(Long id, User currentUser) {
        if (!hasRole(currentUser, "ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can publish events.");
        }
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));
        event.setPublished(true);
        return EventMapper.toDto(eventRepository.save(event));
    }

    @Transactional
    public EventDto unpublishEvent(Long id, User currentUser) {
        if (!hasRole(currentUser, "ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can unpublish events.");
        }
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));
        event.setPublished(false);
        return EventMapper.toDto(eventRepository.save(event));
    }

    @Transactional
    public EventDto changeStatus(Long id, EventStatus status, User currentUser) {
        if (!hasRole(currentUser, "ROLE_ADMIN")) {
            throw new AccessDeniedException("Only admins can change event status.");
        }

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));
        event.setStatus(status);
        if (status == EventStatus.CANCELLED) {
            event.setPublished(false);

            List<EventRegistration> registrations = registrationRepository.findByEventId(id);

            for(EventRegistration reg : registrations){
                User attendee = reg.getUser();

                String subject = "Event Cancelled: " + event.getTitle();
                String body = String.format(
                        "Hi %s,<br><br>We regret to inform you that the event <b>%s</b> scheduled on %s " +
                                "has been <b>cancelled</b>.<br><br>We apologize for the inconvenience.",
                        attendee.getUsername(),
                        event.getTitle(),
                        event.getStartsAt()
                );

                emailEventService.sendEmail(attendee.getEmail(), subject, body);
            }
        }
        return EventMapper.toDto(eventRepository.save(event));
    }

    @Transactional
    public void deleteEvent(Long id, User currentUser) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));

        if (!hasRole(currentUser, "ROLE_ADMIN") &&
                !event.getOrganizer().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You cannot delete this event.");
        }
        eventRepository.delete(event);
    }

    private void validateEvent(CreateEventRequest request) {
        if (request.getStartsAt() == null || request.getEndsAt() == null) {
            throw new IllegalArgumentException("Event must have start and end time.");
        }
        if (!request.getEndsAt().isAfter(request.getStartsAt())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        if (request.getCapacity() != null && request.getCapacity() < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative.");
        }
    }

    private void validateEvent(UpdateEventRequest request) {
        if (request.getStartsAt() == null || request.getEndsAt() == null) {
            throw new IllegalArgumentException("Event must have start and end time.");
        }
        if (!request.getEndsAt().isAfter(request.getStartsAt())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }
        if (request.getCapacity() != null && request.getCapacity() < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative.");
        }
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .map(Role::getName)
                .anyMatch(r -> r.equalsIgnoreCase(roleName));
    }
}
