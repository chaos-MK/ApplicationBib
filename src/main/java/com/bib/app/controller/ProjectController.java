package com.bib.app.controller;

import com.bib.app.dto.ProjectCreateDTO;
import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Company;
import com.bib.app.entities.Project;
import com.bib.app.resolver.ProjectResolver;
import com.bib.app.service.ICompanyService;
import com.bib.app.service.IProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/project", produces = "application/json; charset=UTF-8")
@RequiredArgsConstructor
public class ProjectController {
    private final IProjectService projectService;
    private final ICompanyService companyService;
    private final ProjectResolver projectResolver;

    @PostMapping("/addProject")
    public ResponseEntity<?> addProject(@Valid @RequestBody ProjectCreateDTO projectDTO) {
        try {
            Company company = companyService.getOneCompany(projectDTO.getCompany().getCompanyId());
            if (company == null) {
                log.error("Company not found with ID: {}", projectDTO.getCompany().getCompanyId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Company not found");
            }
            Project project = projectResolver.convertToEntity(projectDTO);
            project.setCompany(company);
            Project savedProject = projectService.add(project);
            ProjectDTO responseDTO = projectResolver.convertToDTO(savedProject);
            log.info("Project created with ID: {}", savedProject.getProjectId());
            return ResponseEntity
                    .created(URI.create("/project/" + savedProject.getProjectId()))
                    .body(responseDTO);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid input for adding project: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error adding project", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateProject(@Valid @RequestBody ProjectDTO projectDTO) {
        try {
            Project project = projectResolver.convertToEntity(projectDTO);
            if (projectDTO.getCompanyId() != null) {
                Company company = companyService.getOneCompany(projectDTO.getCompanyId());
                if (company == null) {
                    log.error("Company not found with ID: {}", projectDTO.getCompanyId());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Company not found");
                }
                project.setCompany(company);
            }
            Project updated = projectService.updateProject(project);
            log.info("Project updated with ID: {}", projectDTO.getProjectId());
            return ResponseEntity.ok(projectResolver.convertToDTO(updated));
        } catch (IllegalArgumentException ex) {
            log.error("Invalid input for updating project: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("Project not found with ID: {}", projectDTO.getProjectId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project not found: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error updating project", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }

    @DeleteMapping("/projectdelete/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id) {
        try {
            Project deleted = projectService.deleteOne(Long.parseLong(id));
            log.info("Project deleted with ID: {}", id);
            return ResponseEntity.ok(projectResolver.convertToDTO(deleted));
        } catch (NumberFormatException ex) {
            log.error("Invalid project ID format: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid project ID format");
        } catch (RuntimeException ex) {
            log.error("Project not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project not found: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error deleting project", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllProjects() {
        try {
            projectService.deleteAllProjects();
            log.info("All projects deleted successfully");
            return ResponseEntity.ok("All projects deleted successfully");
        } catch (Exception ex) {
            log.error("Failed to delete all projects", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: Failed to delete projects");
        }
    }

    @GetMapping("/getOne")
    public ResponseEntity<?> getOneProject(@RequestParam Long id) {
        try {
            ProjectDTO project = projectService.getOneProject(id);
            log.info("Retrieved project with ID: {}", id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException ex) {
            log.error("Project not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project not found: " + ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        try {
            List<ProjectDTO> projects = projectService.getAllProject();
            log.info("Retrieved {} projects", projects.size());
            return ResponseEntity.ok(projects);
        } catch (Exception ex) {
            log.error("Unexpected error retrieving all projects", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }

    @GetMapping("/searchByCompany")
    public ResponseEntity<?> getProjectsByCompany(@RequestParam Long companyId) {
        try {
            List<Project> projects = projectService.getProjectsByCompanyId(companyId);
            log.info("Retrieved {} projects for company ID: {}", projects.size(), companyId);
            return ResponseEntity.ok(projectResolver.convertToDTO(projects));
        } catch (Exception ex) {
            log.error("Unexpected error retrieving projects for company ID: {}", companyId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }
}