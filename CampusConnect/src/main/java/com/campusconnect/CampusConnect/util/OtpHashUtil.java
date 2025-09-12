package com.campusconnect.CampusConnect.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class OtpHashUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String hashOtp(String otp){
        return passwordEncoder.encode(otp);
    }

    public static boolean verifyOtp(String rawOtp,String hashedOtp){
        return passwordEncoder.matches(rawOtp,hashedOtp);
    }
}
