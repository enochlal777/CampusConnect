package com.campusconnect.CampusConnect.service;

import com.campusconnect.CampusConnect.entity.RefreshToken;
import com.campusconnect.CampusConnect.entity.User;
import com.campusconnect.CampusConnect.repository.RefreshTokenRepository;
import com.campusconnect.CampusConnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository  refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;


    public RefreshToken createOrUpdateRefreshToken(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        RefreshToken token;
       if(existingToken.isPresent()){
           token = existingToken.get();
           if(token.getExpiryDate().isBefore(Instant.now())){
               token.setToken(generateRandomToken());
               token.setExpiryDate(Instant.now().plusSeconds(3600));
               refreshTokenRepository.save(token);
           }
       }
       else{
           token = RefreshToken.builder()
                   .token(generateRandomToken())
                   .expiryDate(Instant.now().plusSeconds(3600))
                   .user(user)
                   .build();
       }
       refreshTokenRepository.save(token);

       return token;
    }

    private String generateRandomToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if (token.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please log in again");
        }
        return token;
    }

    public Optional<RefreshToken> verifyAndGetByToken(String rawToken){
        return refreshTokenRepository.findByToken(rawToken)
                .map(this::verifyExpiration);
    }

    @Transactional
    public void deleteByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenRepository.deleteByUser(user);
    }
}
