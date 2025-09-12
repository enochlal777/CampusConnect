package com.campusconnect.CampusConnect.security;

import com.campusconnect.CampusConnect.entity.RefreshToken;
import com.campusconnect.CampusConnect.entity.Role;
import com.campusconnect.CampusConnect.entity.User;
import com.campusconnect.CampusConnect.repository.RoleRepository;
import com.campusconnect.CampusConnect.repository.UserRepository;
import com.campusconnect.CampusConnect.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuthUser = (OAuth2User) authentication.getPrincipal();
        String email = oAuthUser.getAttribute("email");
        String name = oAuthUser.getAttribute("name");

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );
        String providerAccessToken = client.getAccessToken().getTokenValue();
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(email);
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProvider("GOOGLE");
            newUser.setPassword("OAUTH_USER");

            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));
            newUser.getRoles().add(userRole);
            return newUser;
        });

        user.setProviderToken(providerAccessToken);
        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user.getUsername());

        RefreshToken refreshTokenObj = refreshTokenService.createOrUpdateRefreshToken(user.getUsername());

        String rawRefreshToken = refreshTokenObj.getToken();

        response.setContentType("application/json");
        response.getWriter().write(
                String.format("{\"accessToken\": \"%s\", \"refreshToken\": \"%s\"}",
                        accessToken, rawRefreshToken)
        );
    }
}
