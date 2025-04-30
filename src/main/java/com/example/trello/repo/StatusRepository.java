package com.example.trello.repo;

import com.example.trello.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    @Query("select s from Status s where s.is_active=:active ORDER BY s.position_number")
    List<Status> findByIs_active(boolean active);
    Status findByPosition_number(int position_number);
}
