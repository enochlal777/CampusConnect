package com.campusconnect.CampusConnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "password_reset_otp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_hash",nullable = false,length = 255)
    private String otpHash;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expires_at",nullable = false)
    private Instant expiresAt;

    @Column(name = "used",nullable = false)
    private boolean used = false;

    @Column(name = "created_at",nullable = false,updatable = false)
    private Instant createdAt = Instant.now();

    public boolean isExpired(){
        return Instant.now().isAfter(expiresAt);
    }
}
