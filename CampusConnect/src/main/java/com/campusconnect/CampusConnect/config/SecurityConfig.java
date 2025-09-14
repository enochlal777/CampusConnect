package com.campusconnect.CampusConnect.config;

import com.campusconnect.CampusConnect.security.JwtFilter;
import com.campusconnect.CampusConnect.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
@Order(1)
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean(name = "customFilterChain")
    public SecurityFilterChain customFilterChain(HttpSecurity http) throws Exception{
       http
               .securityMatcher("/auth/**", "/api/**")
               .authorizeHttpRequests(auth -> auth
                       .requestMatchers("/api/events/**").permitAll()
                       .requestMatchers("/api/gemini/**").permitAll()
                       .requestMatchers("/api/password/**").permitAll()
                       .requestMatchers("/auth/**").permitAll()
                       .requestMatchers("/api/test/**").permitAll()
                       .requestMatchers("/auth/refresh").permitAll()
                       .requestMatchers("/api/admin/**").hasRole("ADMIN")
                       .requestMatchers("/api/user/**").hasAnyRole("USER","ADMIN")
                       .requestMatchers(HttpMethod.POST,"/api/posts/**").hasAuthority("post:create")
                       .requestMatchers(HttpMethod.PUT,"/api/posts/**").hasAuthority("post:edit")
                       .requestMatchers(HttpMethod.DELETE,"/api/posts/**").hasAuthority("post:delete")
                       .requestMatchers(HttpMethod.POST,"/api/comments/**").hasAuthority("comment:create")
                       .requestMatchers(HttpMethod.DELETE,"/api/comments/**").hasAuthority("comment:delete")
                       .anyRequest().authenticated()
               )
               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               )
               .csrf(csrf -> csrf.disable());
       http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
       return http.build();
    }
}
