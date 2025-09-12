package com.campusconnect.CampusConnect.service;

import com.campusconnect.CampusConnect.entity.PasswordResetOtp;
import com.campusconnect.CampusConnect.entity.User;
import com.campusconnect.CampusConnect.repository.PasswordResetOtpRepository;
import com.campusconnect.CampusConnect.repository.UserRepository;
import com.campusconnect.CampusConnect.util.OtpHashUtil;
import com.campusconnect.CampusConnect.util.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PasswordResetService {

   private final UserRepository userRepository;
   private final PasswordResetOtpRepository otpRepository;
   private final EmailService emailService;
   private final PasswordEncoder passwordEncoder;

   @Autowired
    public PasswordResetService(UserRepository userRepository, PasswordResetOtpRepository otpRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void generateAndSendOtp(String email){
       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("No user found with email: " + email));

       String rawOtp = OtpUtil.generateOtp();
       String hashedOtp = OtpHashUtil.hashOtp(rawOtp);
        Instant expiryTime = Instant.now().plusSeconds(600);

        PasswordResetOtp otp = new PasswordResetOtp();
        otp.setOtpHash(hashedOtp);
        otp.setUser(user);
        otp.setExpiresAt(expiryTime);
        otpRepository.save(otp);

        emailService.sendPasswordResetOtp(user.getEmail(), rawOtp);
    }

    public boolean verifyOtp(String email,String rawOtp){
       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("No user found with email: " + email));
       PasswordResetOtp otp = otpRepository.findTopByUserOrderByCreatedAtDesc(user)
               .orElseThrow(() -> new RuntimeException("No OTP found for this user"));

       if(otp.isExpired()){
           throw new RuntimeException("OTP has expired");
       }

       if(otp.isUsed()){
           throw new RuntimeException("OTP already used");
       }

       if(!OtpHashUtil.verifyOtp(rawOtp,otp.getOtpHash())){
           throw new RuntimeException("Invalid OTP");
       }

       otp.setUsed(true);
       otpRepository.save(otp);
       return true;
    }

    @Transactional
    public void resetPassword(String email,String newPassword){
       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("No user found with email: " + email));
       PasswordResetOtp otp = otpRepository.findTopByUserOrderByCreatedAtDesc(user)
                       .orElseThrow(() -> new RuntimeException("OTP not found"));

       if(otp.isExpired()){
           otpRepository.delete(otp);
           throw new RuntimeException("OTP has expired");
       }
       if(otp.isUsed()){
           otpRepository.delete(otp);
           throw new RuntimeException("OTP already used");
       }

       user.setPassword(passwordEncoder.encode(newPassword));
       userRepository.save(user);

       otpRepository.delete(otp);
    }
}
