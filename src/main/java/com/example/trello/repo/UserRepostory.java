package com.example.trello.repo;

import com.example.trello.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepostory extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
