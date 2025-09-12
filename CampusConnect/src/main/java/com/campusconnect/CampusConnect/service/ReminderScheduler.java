package com.campusconnect.CampusConnect.service;

import com.campusconnect.CampusConnect.entity.Event;
import com.campusconnect.CampusConnect.entity.EventRegistration;
import com.campusconnect.CampusConnect.repository.EventRegistrationRepository;
import com.campusconnect.CampusConnect.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReminderScheduler {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private EmailEventService emailEventService;

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void sendEventReminders(){
        Instant now = Instant.now();
        Instant upcoming = now.plus(30, ChronoUnit.MINUTES);

        List<Event> events = eventRepository.findByStartsAtBetweenAndReminderSentFalse(now,upcoming);

        for(Event event : events){
            List<EventRegistration> registrations = registrationRepository.findByEventId(event.getId());

            for(EventRegistration reg : registrations){
                String subject = "Reminder: Event " + event.getTitle() + " is starting soon!";
                String body = String.format(
                        "Hi %s,<br><br>This is a reminder that the event <b>%s</b> will start at %s.<br><br>See you there!",
                        reg.getUser().getUsername(),
                        event.getTitle(),
                        event.getStartsAt()
                );
                emailEventService.sendEmail(reg.getUser().getEmail(),subject,body);
            }
            event.setReminderSent(true);
            eventRepository.save(event);
        }
    }
}
