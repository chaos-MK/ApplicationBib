package com.bib.app.repository;

import com.bib.app.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Count users for a specific project
    @Query("SELECT COUNT(u) FROM Project p " +
           "JOIN p.cohorts c " +
           "JOIN c.users u " +
           "WHERE p.projectId = :projectId")
    Long countUsersByProjectId(@Param("projectId") Long projectId);

    // Count sessions for a specific project
    @Query("SELECT COUNT(s) FROM Project p " +
           "JOIN p.cohorts c " +
           "JOIN c.users u " +
           "JOIN u.sessions s " +
           "WHERE p.projectId = :projectId")
    Long countSessionsByProjectId(@Param("projectId") Long projectId);

    // Count cohorts for a specific project
    @Query("SELECT COUNT(c) FROM Project p " +
           "JOIN p.cohorts c " +
           "WHERE p.projectId = :projectId")
    Long countCohortsByProjectId(@Param("projectId") Long projectId);

    // Get project with only company information (minimal fetch)
    @Query("SELECT p FROM Project p " +
           "LEFT JOIN FETCH p.company " +
           "WHERE p.projectId = :projectId")
    Optional<Project> findByIdWithCompany(@Param("projectId") Long projectId);

    // Get all projects with only company information (minimal fetch)
    @Query("SELECT p FROM Project p " +
           "LEFT JOIN FETCH p.company")
    List<Project> findAllWithCompany();

    // Batch query to get user counts for multiple projects
    @Query("SELECT p.projectId, COUNT(u) FROM Project p " +
           "LEFT JOIN p.cohorts c " +
           "LEFT JOIN c.users u " +
           "WHERE p.projectId IN :projectIds " +
           "GROUP BY p.projectId")
    List<Object[]> countUsersByProjectIds(@Param("projectIds") List<Long> projectIds);

    // Batch query to get session counts for multiple projects
    @Query("SELECT p.projectId, COUNT(s) FROM Project p " +
           "LEFT JOIN p.cohorts c " +
           "LEFT JOIN c.users u " +
           "LEFT JOIN u.sessions s " +
           "WHERE p.projectId IN :projectIds " +
           "GROUP BY p.projectId")
    List<Object[]> countSessionsByProjectIds(@Param("projectIds") List<Long> projectIds);

    // Batch query to get cohort counts for multiple projects
    @Query("SELECT p.projectId, COUNT(c) FROM Project p " +
           "LEFT JOIN p.cohorts c " +
           "WHERE p.projectId IN :projectIds " +
           "GROUP BY p.projectId")
    List<Object[]> countCohortsByProjectIds(@Param("projectIds") List<Long> projectIds);

    @Query("SELECT p FROM Project p WHERE p.company.companyId = :companyId")
    List<Project> findProjectsByCompanyId(@Param("companyId") Long companyId);

}