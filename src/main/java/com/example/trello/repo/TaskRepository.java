package com.example.trello.repo;

import com.example.trello.entity.Task;
import com.example.trello.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Task findById(Integer id);

    List<Task> findByUser(User user);
}
