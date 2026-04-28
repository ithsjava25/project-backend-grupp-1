package org.group1.projectbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/css/**", "/js/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tickets/new").hasAnyRole("USER", "ADMIN", "HANDLER")
                        .requestMatchers(HttpMethod.POST, "/tickets").hasAnyRole("USER", "ADMIN", "HANDLER")
                        .requestMatchers(HttpMethod.POST, "/api/tickets").hasAnyRole("USER", "ADMIN", "HANDLER")
                        .requestMatchers("/activitylogs/**").hasAnyRole("ADMIN", "HANDLER")
                        .requestMatchers("/comments/**").hasAnyRole("ADMIN", "HANDLER")
                        .requestMatchers("/api/documents/**").hasAnyRole("ADMIN", "HANDLER")
                        .requestMatchers("/api/tickets/**").hasAnyRole("ADMIN", "HANDLER")
                        .requestMatchers("/documents/**").hasAnyRole("ADMIN", "HANDLER")
                        .requestMatchers("/tickets/**").hasAnyRole("ADMIN", "HANDLER")
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
