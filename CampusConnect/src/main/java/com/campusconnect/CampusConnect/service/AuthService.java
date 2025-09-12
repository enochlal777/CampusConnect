package com.campusconnect.CampusConnect.service;

import com.campusconnect.CampusConnect.dto.AuthResponse;
import com.campusconnect.CampusConnect.dto.LoginRequest;
import com.campusconnect.CampusConnect.dto.RegisterRequest;
import com.campusconnect.CampusConnect.entity.RefreshToken;
import com.campusconnect.CampusConnect.entity.Role;
import com.campusconnect.CampusConnect.entity.User;
import com.campusconnect.CampusConnect.repository.RoleRepository;
import com.campusconnect.CampusConnect.repository.UserRepository;
import com.campusconnect.CampusConnect.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private RoleRepository roleRepository;

    public AuthResponse register(RegisterRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setProvider("local");
        String requestedRole = (request.getRole() != null && !request.getRole().isBlank()) ? request.getRole() : "USER";
        final String roleName = requestedRole.startsWith("ROLE_")
                ? requestedRole
                : "ROLE_" + requestedRole;
        Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        user.getRoles().add(role);

        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user.getUsername());

        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user.getUsername());
        return new AuthResponse(accessToken,refreshToken.getToken());
    }

    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!"local" .equalsIgnoreCase(user.getProvider())){
            throw new RuntimeException("Please login using " + user.getProvider());
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));

        String accessToken = jwtUtil.generateToken(user.getUsername());


        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user.getUsername());

        return new AuthResponse(accessToken,refreshToken.getToken());
    }

    public AuthResponse refreshAccessToken(String rawRefreshToken){
        RefreshToken existingToken = refreshTokenService.verifyAndGetByToken(rawRefreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        String username = existingToken.getUser().getUsername();

        refreshTokenService.deleteByUsername(username);

        RefreshToken newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(username);

        String newAccessToken = jwtUtil.generateToken(username);

        return new AuthResponse(newAccessToken,newRefreshToken.getToken());
    }

    public void logout(String username){
        User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenService.deleteByUsername(username);

        if("google".equalsIgnoreCase(user.getProvider()) && user.getProviderToken() !=null){
            revokeGoogleToken(user.getProviderToken());
        }
    }
    private void revokeGoogleToken(String accessToken){
        String revokeUrl = "https://oauth2.googleapis.com/revoke?token=" + accessToken;
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.postForEntity(revokeUrl,null,String.class);
            if(response.getStatusCode().is2xxSuccessful()){
                System.out.println("Google token revoked successfully.");
            }
            else{
                System.out.println("Failed to revoke Google token. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Error revoking Goggle token: " + e.getMessage());
        }
    }
}
