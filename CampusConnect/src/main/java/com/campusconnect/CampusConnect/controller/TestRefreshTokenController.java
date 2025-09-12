package com.campusconnect.CampusConnect.controller;

import com.campusconnect.CampusConnect.entity.RefreshToken;
import com.campusconnect.CampusConnect.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/test/refresh")
public class TestRefreshTokenController {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/create/{username}")
    public RefreshToken createToken(@PathVariable String username) {
        return refreshTokenService.createOrUpdateRefreshToken(username);
    }

    @GetMapping("/find/{token}")
    public Optional<RefreshToken> findToken(@PathVariable String token) {
        return refreshTokenService.verifyAndGetByToken(token);
    }

    @DeleteMapping("/delete/{username}")
    public String deleteByUsername(@PathVariable String username) {
        refreshTokenService.deleteByUsername(username); // Example usage
        return "Deleted token(s) for user: " + username;
    }
}
