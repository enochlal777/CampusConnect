package com.campusconnect.CampusConnect.repository;

import com.campusconnect.CampusConnect.entity.Notification;
import com.campusconnect.CampusConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}
