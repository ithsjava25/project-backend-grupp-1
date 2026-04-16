package org.group1.projectbackend.repository;

import org.group1.projectbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Returns a user by email if it exists
    Optional<User> findByEmail(String email);

    // Returns a user by username if it exists
    Optional<User> findByUsername(String username);

}