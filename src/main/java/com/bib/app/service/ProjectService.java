package com.bib.app.service;

import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Project;
import com.bib.app.repository.ProjectRepository;
import com.bib.app.resolver.ProjectResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService implements  IProjectService{
    private final ProjectRepository projectRepository;
    private final ProjectResolver projectResolver;
    
    public ProjectService(ProjectRepository projectRepository, ProjectResolver projectResolver) {
    	this.projectRepository = projectRepository;
    	this.projectResolver = projectResolver;
    }

    @Override
    public Project add(Project project) {
        return  this.projectRepository.save(project);
    }

    @Override
    public Project Deleteone(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + projectId));

        projectRepository.deleteById(projectId);
        return project;
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
