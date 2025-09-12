package com.campusconnect.CampusConnect.repository;

import com.campusconnect.CampusConnect.entity.PasswordResetOtp;
import com.campusconnect.CampusConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp,Long> {

    Optional<PasswordResetOtp> findTopByUserOrderByCreatedAtDesc(User user);

    Optional<PasswordResetOtp> findByOtpHash(String otpHash);
}
