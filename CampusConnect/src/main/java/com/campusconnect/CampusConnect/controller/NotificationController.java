package com.campusconnect.CampusConnect.controller;

import com.campusconnect.CampusConnect.dto.NotificationDto;
import com.campusconnect.CampusConnect.entity.User;
import com.campusconnect.CampusConnect.repository.UserRepository;
import com.campusconnect.CampusConnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails){
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUserNotifications(@AuthenticationPrincipal UserDetails userDetails){
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(notificationService.getUserNotifications(currentUser));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id,@AuthenticationPrincipal UserDetails userDetails){
        User currentUser = getCurrentUser(userDetails);
        notificationService.markAsRead(id,currentUser);
        return ResponseEntity.ok().build();
    }
}
