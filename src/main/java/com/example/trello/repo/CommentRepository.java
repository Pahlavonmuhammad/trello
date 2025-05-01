package com.example.trello.repo;

import com.example.trello.entity.Comment;
import com.example.trello.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    void deleteByUser(User user);
}
