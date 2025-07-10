package com.bib.app.controller;

import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Project;

import com.bib.app.service.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.net.URI;
@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
public class ProjectController {
    private final IProjectService projectService ;
    
    public ProjectController(IProjectService projectService) {
    	this.projectService = projectService;
    }
    
    @PostMapping("/addProject")
    public ResponseEntity<?> add(@RequestBody Project project) {
        try {
            Project savedProject = projectService.add(project);

            if (savedProject == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\":\"Project creation failed. Service returned null.\"}");
            }

            URI location = URI.create("/projects/" + savedProject.getProjectId());

            return ResponseEntity
                    .created(location)
                    .body(savedProject);

        } catch (Exception ex) {
            ex.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Unexpected error occurred: " + ex.getMessage() + "\"}");
        }
    }
    @DeleteMapping("/projectdelete/{id}")
    public ResponseEntity<?> deleteone(@PathVariable Long id) {
        Project deleted = projectService.Deleteone(id);
        if (deleted != null) {
            return ResponseEntity.ok(deleted);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cohort not found");
        }
    }
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllProjects() {
        projectService.deleteAllProjects();
        return ResponseEntity.ok("All cohorts deleted successfully.");
    }
    
    @GetMapping("/getOne")
    public ResponseEntity<?> getOneProject(@RequestParam Long id) {
        try {
            ProjectDTO project = projectService.getOneProject(id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Unexpected error occurred: " + ex.getMessage() + "\"}");
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllProject() {
        try {
            List<ProjectDTO> projects = projectService.getAllProject();
            return ResponseEntity.ok(projects);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Unexpected error occurred: " + ex.getMessage() + "\"}");
        }
    }
    
    /*
    @GetMapping("/getOne")
    public ResponseEntity<Project> getOneProject(@RequestParam Long id) {
        Project project = projectService.getOneProject(id);
        return ResponseEntity.ok(project);
    }
    @GetMapping
    public ResponseEntity<List<Project>> getAllProject() {
        return ResponseEntity.ok(projectService.getAllProject());
    }
    */


}
