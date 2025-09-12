package com.campusconnect.CampusConnect.repository;

import com.campusconnect.CampusConnect.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration,Long> {
    Optional<EventRegistration> findByEventIdAndUserId(Long eventId,Long userId);
    List<EventRegistration> findByEventId(Long eventId);
    List<EventRegistration> findByUserId(Long userId);
}
