package com.campusconnect.CampusConnect.controller;

import com.campusconnect.CampusConnect.dto.ApiResponse;
import com.campusconnect.CampusConnect.dto.ForgotPasswordRequest;
import com.campusconnect.CampusConnect.dto.ResetPasswordRequest;
import com.campusconnect.CampusConnect.dto.VerifyOtpRequest;
import com.campusconnect.CampusConnect.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request){
        passwordResetService.generateAndSendOtp(request.getEmail());
        return ResponseEntity.ok(new ApiResponse("OTP sent to your email."));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody VerifyOtpRequest request){
        passwordResetService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(new ApiResponse("OTP verified successfully."));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request){
        passwordResetService.resetPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(new ApiResponse("Password reset successfully"));
    }
}
