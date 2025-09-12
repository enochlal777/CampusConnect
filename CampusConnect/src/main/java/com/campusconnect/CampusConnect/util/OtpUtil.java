package com.campusconnect.CampusConnect.util;

import java.security.SecureRandom;

public class OtpUtil {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public static String generateOtp(){
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }
}
