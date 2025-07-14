package com.bib.app.service;

import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Project;
import com.bib.app.repository.CompanyRepository;
import com.bib.app.repository.ProjectRepository;
import com.bib.app.resolver.ProjectResolver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService implements  IProjectService{
    private final ProjectRepository projectRepository;
    private final ProjectResolver projectResolver;
    private final CompanyRepository companyRepository;




    @Override
    public Project updateProject(Project project) {
        Optional<Project> existingOpt = projectRepository.findById(project.getProjectId());

        if (existingOpt.isPresent()) {
            Project existing = existingOpt.get();

            existing.setProjectName(project.getProjectName());
            existing.setProjectWebsite(project.getProjectWebsite());
            existing.setStartDate(project.getStartDate());
            existing.setEndDate(project.getEndDate());
            existing.setDuration(project.getDuration());
            existing.setOverallStatus(project.getOverallStatus());
            existing.setHasGraphs(project.getHasGraphs());
            existing.setHasDashboard(project.getHasDashboard());

            // Optionnel : mettre à jour la company si nécessaire
            if (project.getCompany() != null) {
                existing.setCompany(project.getCompany());
            }

            return projectRepository.save(existing);
        } else {
            throw new EntityNotFoundException("Project with ID " + project.getProjectId() + " not found.");
        }
    }

    @Override
    public Project deleteOne(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + projectId));

        projectRepository.deleteById(projectId);
        return project;
    }



    @Override
    public Project add(Project project) {
        return  this.projectRepository.save(project);
    }



    @Override
    public void deleteAllProjects() {
        projectRepository.deleteAll();

    }
    
    @Override
    @Transactional(readOnly = true)
    public ProjectDTO getOneProject(Long id) {
        // Fetch project with minimal data (only company)
        Project project = projectRepository.findByIdWithCompany(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
        
        // Resolver will handle count queries separately
        return projectResolver.convertToDTO(project);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProject() {
        // Fetch all projects with minimal data (only company)
        List<Project> projects = projectRepository.findAllWithCompany();
        
        // Resolver will handle batch count queries
        return projectResolver.convertToDTO(projects);
    }

    @Override
    public List<Project> getProjectsByCompanyId(Long companyId) {
        return projectRepository.findProjectsByCompanyId(companyId);
    }

   /* @Override
    public Project getOneProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + id));
    }

    @Override
    public List<Project> getAllProject() {
        return (List<Project>) projectRepository.findAll();
    }*/

}
