package com.bib.app.service;


import com.bib.app.controller.CohortCreateDTO;
import com.bib.app.dto.CohortDTO;
import com.bib.app.dto.UserDTO;
import com.bib.app.entities.Cohort;
import com.bib.app.entities.Project;
import com.bib.app.repository.CohortRepository;
import com.bib.app.repository.ProjectRepository;
import com.bib.app.resolver.CohortResolver;
import com.bib.app.resolver.UserResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CohortService implements ICohortService {
    private final CohortRepository cohortRepository;
    private final ProjectRepository projectRepository;
    private final CohortResolver cohortResolver;
    private final UserResolver userResolver; // Added dependency

    @Transactional
    public CohortDTO add(CohortCreateDTO createDTO) {
        Project project = projectRepository.findById(createDTO.getProject().getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + createDTO.getProject().getProjectId()));
        Cohort cohort = cohortResolver.convertToEntity(createDTO);
        cohort.setProject(project);
        Cohort saved = cohortRepository.save(cohort);
        return cohortResolver.convertToDTO(saved);
    }

    @Transactional
    public CohortDTO deleteOne(Long cohortId) {
        Cohort cohort = cohortRepository.findByIdWithProject(cohortId)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + cohortId));
        cohortRepository.deleteById(cohortId);
        return cohortResolver.convertToDTO(cohort);
    }

    @Transactional(readOnly = true)
    public List<CohortDTO> getAllCohorts() {
        List<Cohort> cohorts = cohortRepository.findAllWithProject();
        return cohortResolver.convertToDTO(cohorts);
    }

    @Transactional
    public void deleteAllCohorts() {
        cohortRepository.deleteAll();
    }

    @Transactional(readOnly = true)
    public CohortDTO getOneCohort(Long id) {
        Cohort cohort = cohortRepository.findByIdWithProject(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + id));
        return cohortResolver.convertToDTO(cohort);
    }

    @Transactional(readOnly = true)
    public List<CohortDTO> searchByProject(Long projectId) {
        List<Cohort> cohorts = cohortRepository.findByProjectId(projectId);
        return cohortResolver.convertToDTO(cohorts);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByCohortId(Long cohortId) {
        Cohort cohort = cohortRepository.findByIdWithProject(cohortId)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + cohortId));
        return userResolver.convertToDTO(cohort.getUsers());
    }
}