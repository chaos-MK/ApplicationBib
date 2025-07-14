package com.bib.app.repository;

import com.bib.app.entities.Cohort;
import com.bib.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CohortRepository extends JpaRepository<Cohort, Long> {
    @Query("SELECT c FROM Cohort c LEFT JOIN FETCH c.project WHERE c.cohortId = :id")
    Optional<Cohort> findByIdWithProject(@Param("id") Long id);

    @Query("SELECT c FROM Cohort c LEFT JOIN FETCH c.project")
    List<Cohort> findAllWithProject();

    @Query("SELECT c FROM Cohort c WHERE c.project.projectId = :projectId")
    List<Cohort> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT u FROM User u WHERE u.cohort.cohortId = :cohortId")
    List<User> findUsersByCohortId(@Param("cohortId") Long cohortId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.cohort.cohortId = :cohortId")
    Long countUsersByCohortId(@Param("cohortId") Long cohortId);

    @Query("SELECT COUNT(s) FROM Stage s WHERE s.cohort.cohortId = :cohortId")
    Long countStagesByCohortId(@Param("cohortId") Long cohortId);

    @Query("SELECT c.cohortId, COUNT(u) FROM Cohort c LEFT JOIN c.users u GROUP BY c.cohortId")
    List<Object[]> countUsersByCohortIds(List<Long> cohortIds);

    @Query("SELECT c.cohortId, COUNT(s) FROM Cohort c LEFT JOIN c.stages s GROUP BY c.cohortId")
    List<Object[]> countStagesByCohortIds(List<Long> cohortIds);
}