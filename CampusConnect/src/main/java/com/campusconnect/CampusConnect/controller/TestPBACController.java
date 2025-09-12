package com.campusconnect.CampusConnect.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestPBACController {

    @GetMapping("/read")
    @PreAuthorize("hasAuthority('user:read')")
    public String readData(){
        return "You can READ DATA!";
    }

    @PostMapping("/write")
    @PreAuthorize("hasAuthority('user:write')")
    public String writeData(){
        return "You can WRITE DATA!";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin:read')")
    public String adminData(){
        return "You can READ admin data!";
    }
    @PreAuthorize("hasAuthority('post:create')")
    @PostMapping("/posts")
    public String createPost() {
        return "Post created!";
    }

    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/users")
    public String getUsers() {
        return "User list";
    }

}
