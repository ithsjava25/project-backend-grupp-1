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

        jdbcTemplate.update("""
    INSERT INTO users (id, username, email, password, full_name, enabled, created_at)
    VALUES
      (1, 'user', 'user@example.com', 'password', 'user', true, CURRENT_TIMESTAMP),
      (2, 'adamaj01', 'adamaj@example.com', 'password', 'Adam', true, CURRENT_TIMESTAMP),
      (3, 'emmtra01', 'emmtra@example.com', 'password', 'Emma', true, CURRENT_TIMESTAMP),
      (4, 'erifa101', 'erifa1@example.com', 'password', 'Erika', true, CURRENT_TIMESTAMP),
      (5, 'johjan01', 'johjan@example.com', 'password', 'Johan', true, CURRENT_TIMESTAMP)
""");
    }
}
