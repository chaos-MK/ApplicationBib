package com.bib.app.repository;
import com.bib.app.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    
    // Method to find sessions by user ID
    List<Session> findByUserUserId(Long userId);
    
}