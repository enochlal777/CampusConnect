package com.campusconnect.CampusConnect.repository;

import com.campusconnect.CampusConnect.entity.Event;
import com.campusconnect.CampusConnect.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {

    List<Event> findByStatus(EventStatus status);

    List<Event> findByPublishedTrue();

    List<Event> findByStartsAtAfter(Instant now);

    List<Event> findByEndsAtBefore(Instant now);

    List<Event> findByCategoryIgnoreCase(String category);

    List<Event> findByOrganizer_UsernameIgnoreCase(String username);

    List<Event> findByPublishedTrueAndCategoryIgnoreCase(String category);

    @Query("SELECT e FROM Event e " +
            "WHERE e.published = true " +
            "AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Event> searchPublishedEventsByKeyword(String keyword);

    List<Event> findByLocationIgnoreCase(String location);

    List<Event> findByStartsAtBetween(Instant start,Instant end);

    List<Event> findByStartsAtBetweenAndReminderSentFalse(Instant start,Instant end);

}
