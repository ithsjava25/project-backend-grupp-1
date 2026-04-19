package org.group1.projectbackend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        Integer userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = 1",
                Integer.class
        );

        if (userCount != null && userCount > 0) {
            return;
        }

        jdbcTemplate.update(
                """
                INSERT INTO users (id, username, email, password, full_name, enabled, created_at)
                VALUES (1, 'testuser', 'testuser@example.com', 'password123', 'Test User', true, CURRENT_TIMESTAMP)
                """
        );
    }
}
