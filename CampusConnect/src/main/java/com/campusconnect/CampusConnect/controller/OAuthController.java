package com.campusconnect.CampusConnect.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

    @GetMapping("/")
    public String home() {
        return "Welcome to CampusConnect!";
    }

    @GetMapping("/secured")
    public String secured() {
        return "You are logged in via OAuth!";
    }
}
