package org.group1.projectbackend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Kryptera lösenord
        String userPassword = passwordEncoder.encode("password123");
        String adminPassword = passwordEncoder.encode("admin123");

        //  USER
        jdbcTemplate.update("""
            INSERT INTO users (id, username, email, password, full_name, enabled, created_at)
            VALUES (1, 'testuser', 'testuser@example.com', ?, 'Test User', true, CURRENT_TIMESTAMP)
            ON CONFLICT (id) DO UPDATE SET
              username = EXCLUDED.username,
              email = EXCLUDED.email,
              password = EXCLUDED.password,
              full_name = EXCLUDED.full_name,
              enabled = EXCLUDED.enabled
        """, userPassword);

        // ADMIN USER
        jdbcTemplate.update("""
            INSERT INTO users (id, username, email, password, full_name, enabled, created_at)
            VALUES (2, 'admin', 'admin@example.com', ?, 'Admin User', true, CURRENT_TIMESTAMP)
            ON CONFLICT (id) DO UPDATE SET
              username = EXCLUDED.username,
              email = EXCLUDED.email,
              password = EXCLUDED.password,
              full_name = EXCLUDED.full_name,
              enabled = EXCLUDED.enabled
        """, adminPassword);

        //  ROLLER
        jdbcTemplate.update("""
            INSERT INTO roles (id, name)
            VALUES (1, 'ROLE_USER')
            ON CONFLICT (id) DO NOTHING
        """);

        jdbcTemplate.update("""
            INSERT INTO roles (id, name)
            VALUES (2, 'ROLE_ADMIN')
            ON CONFLICT (id) DO NOTHING
        """);

        //  Koppla USER → ROLE_USER
        jdbcTemplate.update("""
            INSERT INTO user_roles (user_id, role_id)
            VALUES (1, 1)
            ON CONFLICT DO NOTHING
        """);

        //  Koppla ADMIN → ROLE_ADMIN
        jdbcTemplate.update("""
            INSERT INTO user_roles (user_id, role_id)
            VALUES (2, 2)
            ON CONFLICT DO NOTHING
        """);
    }
}
