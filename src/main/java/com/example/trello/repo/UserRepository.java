package com.example.trello.repo;

import com.example.trello.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    List<User> getUsersById(Integer id);

    List<User> getUserById(Integer id, Sort sort);
}
