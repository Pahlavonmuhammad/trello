package com.example.trello.repo;

import com.example.trello.entity.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Integer> {
    @Query("select s from Status s where s.is_active=:active ORDER BY s.position_number")
    List<Status> findByIs_active(boolean active);
    @Query("select s from Status s where s.position_number=:position")
    Status findByPosition_number(int position);
    @Query(value = "SELECT * FROM status WHERE position_number > :position AND is_active = true ORDER BY position_number ASC LIMIT 1", nativeQuery = true)
    Status findFirstByPositionGreaterThan(@Param("position") Integer position);

    @Query(value = "SELECT * FROM status WHERE position_number < :position AND is_active = true ORDER BY position_number DESC LIMIT 1", nativeQuery = true)
    Status findFirstByPositionSmallerThan(@Param("position") Integer position);


}
