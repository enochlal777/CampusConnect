package com.campusconnect.CampusConnect.service;

import com.campusconnect.CampusConnect.entity.PasswordResetOtp;
import com.campusconnect.CampusConnect.repository.PasswordResetOtpRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OtpCleanUpService {

    private final PasswordResetOtpRepository otpRepository;

    public OtpCleanUpService(PasswordResetOtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanUpExpiredOtps(){
        Instant now = Instant.now();
        List<PasswordResetOtp> expiredOtps = otpRepository.findAll()
                .stream()
                .filter(otp -> otp.isExpired() || otp.isUsed())
                .toList();
        if(!expiredOtps.isEmpty()){
            otpRepository.deleteAll(expiredOtps);
            System.out.println("🧹 Cleaned up " + expiredOtps.size());
        }
    }
}
