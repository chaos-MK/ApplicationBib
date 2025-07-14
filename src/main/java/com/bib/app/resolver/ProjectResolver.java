package com.bib.app.resolver;

import com.bib.app.dto.ProjectCreateDTO;
import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Project;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectResolver {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Project convertToEntity(ProjectCreateDTO dto) {
        Project project = new Project();
        project.setProjectName(dto.getProjectName());
        project.setProjectWebsite(dto.getProjectWebsite());
        project.setStartDate(dto.getStartDate() != null ? LocalDate.parse(dto.getStartDate(), DATE_FORMATTER) : null);
        project.setEndDate(dto.getEndDate() != null ? LocalDate.parse(dto.getEndDate(), DATE_FORMATTER) : null);
        project.setDuration(dto.getDuration());
        project.setOverallStatus(dto.getOverallStatus());
        project.setHasGraphs(dto.isHasGraphs());
        project.setHasDashboard(dto.isHasDashboard());
        return project;
    }

    public Project convertToEntity(ProjectDTO dto) {
        Project project = new Project();
        project.setProjectId(Long.parseLong(dto.getProjectId()));
        project.setProjectName(dto.getProjectName());
        project.setProjectWebsite(dto.getProjectWebsite());
        project.setStartDate(dto.getStartDate() != null ? LocalDate.parse(dto.getStartDate(), DATE_FORMATTER) : null);
        project.setEndDate(dto.getEndDate() != null ? LocalDate.parse(dto.getEndDate(), DATE_FORMATTER) : null);
        project.setDuration(dto.getDuration());
        project.setOverallStatus(dto.getOverallStatus());
        project.setHasGraphs(dto.isHasGraphs());
        project.setHasDashboard(dto.isHasDashboard());
        return project;
    }

    public ProjectDTO convertToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setProjectId(String.valueOf(project.getProjectId()));
        dto.setProjectName(project.getProjectName());
        dto.setProjectWebsite(project.getProjectWebsite());
        dto.setStartDate(project.getStartDate() != null ? project.getStartDate().format(DATE_FORMATTER) : null);
        dto.setEndDate(project.getEndDate() != null ? project.getEndDate().format(DATE_FORMATTER) : null);
        dto.setDuration(project.getDuration());
        dto.setOverallStatus(project.getOverallStatus());
        dto.setHasGraphs(project.isHasGraphs());
        dto.setHasDashboard(project.getHasDashboard() != null && project.getHasDashboard());
        dto.setCompanyId(project.getCompany() != null ? project.getCompany().getCompanyId() : null);
        dto.setCompanyName(project.getCompany() != null ? project.getCompany().getCompanyName() : null);
        dto.setCohortCount(project.getCohorts() != null ? project.getCohorts().size() : 0);
        // Set userCount and sessionCount as needed (e.g., from service or related entities)
        dto.setUserCount(0); // Placeholder, update based on actual data
        dto.setSessionCount(0); // Placeholder, update based on actual data
        return dto;
    }

    public List<ProjectDTO> convertToDTO(List<Project> projects) {
        return projects.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}