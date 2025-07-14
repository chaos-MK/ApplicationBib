package com.bib.app.resolver;

import com.bib.app.controller.CohortCreateDTO;
import com.bib.app.dto.CohortDTO;
import com.bib.app.entities.Cohort;
import com.bib.app.repository.CohortRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CohortResolver {
    private final CohortRepository cohortRepository;

    public CohortDTO convertToDTO(Cohort cohort) {
        if (cohort == null) {
            return null;
        }
        // Initialize project to avoid lazy loading issues
        Hibernate.initialize(cohort.getProject());

        CohortDTO dto = new CohortDTO();
        dto.setCohortId(cohort.getCohortId());
        dto.setCohortName(cohort.getCohortName());
        dto.setSoftwareVersion(cohort.getSoftwareVersion());
        dto.setStartDate(cohort.getStartDate() != null ? cohort.getStartDate().toString() : null);
        dto.setEndDate(cohort.getEndDate() != null ? cohort.getEndDate().toString() : null);
        dto.setDuration(cohort.getDuration());
        dto.setOverallStatus(cohort.getOverallStatus());
        dto.setHasGraphs(cohort.getHasGraphs());
        dto.setHasDashboard(cohort.getHasDashboard());
        dto.setProjectId(cohort.getProject() != null ? cohort.getProject().getProjectId() : null);
        dto.setProjectName(cohort.getProject() != null ? cohort.getProject().getProjectName() : null);

        // Fetch counts using repository queries
        dto.setUserCount(cohortRepository.countUsersByCohortId(cohort.getCohortId()).intValue());
        dto.setStageCount(cohortRepository.countStagesByCohortId(cohort.getCohortId()).intValue());

        return dto;
    }

    public List<CohortDTO> convertToDTO(List<Cohort> cohorts) {
        if (cohorts == null || cohorts.isEmpty()) {
            return List.of();
        }

        // Initialize project for all cohorts
        cohorts.forEach(cohort -> Hibernate.initialize(cohort.getProject()));

        // Batch fetch counts for efficiency
        List<Long> cohortIds = cohorts.stream().map(Cohort::getCohortId).collect(Collectors.toList());
        Map<Long, Integer> userCounts = cohortRepository.countUsersByCohortIds(cohortIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
        Map<Long, Integer> stageCounts = cohortRepository.countStagesByCohortIds(cohortIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));

        return cohorts.stream().map(cohort -> {
            CohortDTO dto = new CohortDTO();
            dto.setCohortId(cohort.getCohortId());
            dto.setCohortName(cohort.getCohortName());
            dto.setSoftwareVersion(cohort.getSoftwareVersion());
            dto.setStartDate(cohort.getStartDate() != null ? cohort.getStartDate().toString() : null);
            dto.setEndDate(cohort.getEndDate() != null ? cohort.getEndDate().toString() : null);
            dto.setDuration(cohort.getDuration());
            dto.setOverallStatus(cohort.getOverallStatus());
            dto.setHasGraphs(cohort.getHasGraphs());
            dto.setHasDashboard(cohort.getHasDashboard());
            dto.setProjectId(cohort.getProject() != null ? cohort.getProject().getProjectId() : null);
            dto.setProjectName(cohort.getProject() != null ? cohort.getProject().getProjectName() : null);
            dto.setUserCount(userCounts.getOrDefault(cohort.getCohortId(), 0));
            dto.setStageCount(stageCounts.getOrDefault(cohort.getCohortId(), 0));
            return dto;
        }).collect(Collectors.toList());
    }

    public Cohort convertToEntity(CohortDTO dto) {
        if (dto == null) {
            return null;
        }
        Cohort cohort = new Cohort();
        cohort.setCohortId(dto.getCohortId());
        cohort.setCohortName(dto.getCohortName());
        cohort.setSoftwareVersion(dto.getSoftwareVersion());
        cohort.setStartDate(dto.getStartDate() != null ? LocalDate.parse(dto.getStartDate()) : null);
        cohort.setEndDate(dto.getEndDate() != null ? LocalDate.parse(dto.getEndDate()) : null);
        cohort.setDuration(dto.getDuration());
        cohort.setOverallStatus(dto.getOverallStatus());
        cohort.setHasGraphs(dto.getHasGraphs());
        cohort.setHasDashboard(dto.getHasDashboard());
        // Project is not set here; it should be set by the service using projectId
        return cohort;
    }

    public Cohort convertToEntity(CohortCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        Cohort cohort = new Cohort();
        cohort.setCohortName(createDTO.getCohortName());
        cohort.setSoftwareVersion(createDTO.getSoftwareVersion());
        cohort.setStartDate(createDTO.getStartDate());
        cohort.setEndDate(createDTO.getEndDate());
        cohort.setDuration(createDTO.getDuration());
        cohort.setOverallStatus(createDTO.getState());
        cohort.setHasGraphs(createDTO.isHasGraphs());
        cohort.setHasDashboard(createDTO.isHasDashboard());
        // Project is not set here; it should be set by the service using projectId
        return cohort;
    }


}