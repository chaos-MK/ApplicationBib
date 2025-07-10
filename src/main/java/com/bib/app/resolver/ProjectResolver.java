package com.bib.app.resolver;

import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Project;
import com.bib.app.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectResolver {
    
    private final ProjectRepository projectRepository;
    
    public ProjectResolver(ProjectRepository projectRepository) {
    	this.projectRepository = projectRepository;
    }
    
    public ProjectDTO convertToDTO(Project project) {
        if (project == null) {
            return null;
        }
        
        // Get counts using separate queries
        Long userCount = projectRepository.countUsersByProjectId(project.getProjectId());
        Long sessionCount = projectRepository.countSessionsByProjectId(project.getProjectId());
        Long cohortCount = projectRepository.countCohortsByProjectId(project.getProjectId());
        
        return new ProjectDTO(
            project.getProjectId(),
            project.getProjectName(),
            project.getProjectWebsite(),
            project.getStartDate(),
            project.getEndDate(),
            project.getDuration(),
            project.getOverallStatus(),
            project.getHasGraphs(),
            project.getHasDashboard(),
            project.getCompany() != null ? project.getCompany().getCompanyId() : null,
            project.getCompany() != null ? project.getCompany().getCompanyName() : null,
            userCount != null ? userCount : 0L,
            sessionCount != null ? sessionCount : 0L,
            cohortCount != null ? cohortCount : 0L
        );
    }
    
    public List<ProjectDTO> convertToDTO(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return List.of();
        }
        
        // Extract project IDs for batch queries
        List<Long> projectIds = projects.stream()
                .map(Project::getProjectId)
                .collect(Collectors.toList());
        
        // Get all user counts in one query
        Map<Long, Long> userCounts = projectRepository.countUsersByProjectIds(projectIds)
                .stream()
                .collect(Collectors.toMap(
                    row -> (Long) row[0],
                    row -> (Long) row[1]
                ));
        
        // Get all session counts in one query
        Map<Long, Long> sessionCounts = projectRepository.countSessionsByProjectIds(projectIds)
                .stream()
                .collect(Collectors.toMap(
                    row -> (Long) row[0],
                    row -> (Long) row[1]
                ));
        
        // Get all cohort counts in one query
        Map<Long, Long> cohortCounts = projectRepository.countCohortsByProjectIds(projectIds)
                .stream()
                .collect(Collectors.toMap(
                    row -> (Long) row[0],
                    row -> (Long) row[1]
                ));
        
        // Convert to DTOs using the batch-fetched counts
        return projects.stream()
                .map(project -> new ProjectDTO(
                    project.getProjectId(),
                    project.getProjectName(),
                    project.getProjectWebsite(),
                    project.getStartDate(),
                    project.getEndDate(),
                    project.getDuration(),
                    project.getOverallStatus(),
                    project.getHasGraphs(),
                    project.getHasDashboard(),
                    project.getCompany() != null ? project.getCompany().getCompanyId() : null,
                    project.getCompany() != null ? project.getCompany().getCompanyName() : null,
                    userCounts.getOrDefault(project.getProjectId(), 0L),
                    sessionCounts.getOrDefault(project.getProjectId(), 0L),
                    cohortCounts.getOrDefault(project.getProjectId(), 0L)
                ))
                .collect(Collectors.toList());
    }
    
    public Project convertToEntity(ProjectDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Project project = new Project();
        project.setProjectId(dto.getProjectId());
        project.setProjectName(dto.getProjectName());
        project.setProjectWebsite(dto.getProjectWebsite());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setDuration(dto.getDuration());
        project.setOverallStatus(dto.getOverallStatus());
        project.setHasGraphs(dto.getHasGraphs());
        project.setHasDashboard(dto.getHasDashboard());
        
        return project;
    }
}