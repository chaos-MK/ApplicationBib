package com.bib.app.repository;

import com.bib.app.entities.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CohortRepository extends JpaRepository<Cohort, Long> {
    void deleteAll();
}