package com.campusconnect.CampusConnect.repository;

import com.campusconnect.CampusConnect.entity.RefreshToken;
import com.campusconnect.CampusConnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);
}
