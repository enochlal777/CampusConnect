package com.campusconnect.CampusConnect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from:no-reply@campusconnect}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendPasswordResetOtp(String toEmail, String otp){
        String subject = "Your CampusConnect Password Reset Code";
        String body = "Hi,\n\n" +
                "Use this OTP to reset your password: " + otp + "\n" +
                "This code expires in 10 minutes.\n\n" +
                "If you didn’t request this, you can safely ignore this email.\n\n" +
                "— CampusConnect";

        if(mailEnabled) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setFrom(fromAddress);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                log.info("OTP email sent successfully to {}", toEmail);
            } catch (Exception e) {
                log.error("Failed to send OTP email to {}", toEmail, e);
                throw new RuntimeException("Failed to send email", e);
            }
        }
    }
}
