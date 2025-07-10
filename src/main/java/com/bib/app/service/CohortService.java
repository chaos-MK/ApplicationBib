package com.bib.app.service;

import com.bib.app.entities.Cohort;
import com.bib.app.entities.Project;

import com.bib.app.entities.User;
import com.bib.app.repository.CohortRepository;
import com.bib.app.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CohortService implements ICohortService {

    private final CohortRepository cohortRepository;
    private final ProjectRepository projectRepository;
    
    public CohortService(CohortRepository cohortRepository, ProjectRepository projectRepository) {
    	this.cohortRepository = cohortRepository;
    	this.projectRepository = projectRepository;
    }

    @Override
    public Cohort add(Cohort cohort) {
        return this.cohortRepository.save(cohort);
    }

    @Override
    public Cohort Deleteone(Long cohortId) {
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + cohortId));

        cohortRepository.deleteById(cohortId);
        return cohort;
    }

    @Override
    public List<User> getUsersByCohortId(Long cohortId) {
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + cohortId));
        return cohort.getUsers();
    }

    @Override
    public List<Cohort> searchByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        return project.getCohorts();
    }

    @Override
    public List<Cohort> getAllCohort() {
        return (List<Cohort>) cohortRepository.findAll();

    }
    @Override
    public void deleteAllCohorts() {
        cohortRepository.deleteAll();
    }
    @Override
    public Cohort getOneCohort(Long id) {
        return cohortRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + id));
    }



}








