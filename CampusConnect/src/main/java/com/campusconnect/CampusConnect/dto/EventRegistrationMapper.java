package com.campusconnect.CampusConnect.dto;

import com.campusconnect.CampusConnect.entity.EventRegistration;

public class EventRegistrationMapper {

    public static EventRegistrationDto toDto(EventRegistration registration){
        if(registration==null){
            return null;
        }
        return new EventRegistrationDto(
                registration.getId(),
                AttendeeMapper.toDto(registration.getUser()),
                registration.getEvent().getId(),
                registration.getRegisteredAt()
        );
    }
}
