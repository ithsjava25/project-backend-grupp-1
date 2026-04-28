package org.group1.projectbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())

                // använd Spring default login
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/activitylogs/**").hasRole("ADMIN")
                        .requestMatchers("/comments/**").authenticated()
                        .requestMatchers("/api/tickets/**").authenticated()
                        .anyRequest().permitAll()
                )

                .userDetailsService(userDetailsService)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}