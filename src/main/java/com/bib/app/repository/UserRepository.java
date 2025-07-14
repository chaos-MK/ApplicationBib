package com.bib.app.repository;

import com.bib.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    List<User> findByCohortCohortId(Long cohortId);

    List<User> findByStatus(String status);

    Optional<User> findByUserNameIgnoreCase(String userName);

    List<User> findByHasGraphsTrue();
    
    List<User> findByHasDashboardTrue();

    @Query("SELECT u FROM User u WHERE u.cohort.cohortName = :cohortName")
    List<User> findUsersByCohortName(@Param("cohortName") String cohortName);
    
    boolean existsByUserName(String userName);
}
