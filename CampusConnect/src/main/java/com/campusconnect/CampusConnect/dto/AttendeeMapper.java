package com.campusconnect.CampusConnect.dto;

import com.campusconnect.CampusConnect.entity.User;

public class AttendeeMapper {
    public static AttendeeDto toDto(User user){
        return new AttendeeDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
