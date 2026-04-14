package org.group1.projectbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.group1.projectbackend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
import java.util.Optional;
import org.group1.projectbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
