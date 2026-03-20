package com.example.taskmanager.repository;

import com.example.taskmanager.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);

    @Query("""
        SELECT u FROM User u
        WHERE u.username = :input
           OR u.email = :input
           OR u.phone = :input
    """)
    Optional<User> findByIdentifier(String input);

    boolean existsByPhone(String phone);
}
