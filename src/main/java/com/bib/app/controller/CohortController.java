package com.bib.app.controller;

import com.bib.app.dto.CohortDTO;
import com.bib.app.dto.UserDTO;
import com.bib.app.service.ICohortService;
import com.bib.app.service.IProjectService;
import com.bib.app.resolver.UserResolver;
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
@RequestMapping(value = "/cohort", produces = "application/json; charset=UTF-8")
@RequiredArgsConstructor
public class CohortController {
    private final ICohortService cohortService;
    private final IProjectService projectService;
    private final UserResolver userResolver;

    @PostMapping("/addCohort")
    public ResponseEntity<?> addCohort(@Valid @RequestBody CohortCreateDTO cohortDTO) {
        try {
            CohortDTO savedCohort = cohortService.add(cohortDTO);
            log.info("Cohort created with ID: {}", savedCohort.getCohortId());
            return ResponseEntity
                    .created(URI.create("/cohort/" + savedCohort.getCohortId()))
                    .body(savedCohort);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid input for adding cohort: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("Project not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project not found: " + ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error adding cohort", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCohort(@PathVariable Long id) {
        try {
            CohortDTO deleted = cohortService.deleteOne(id);
            log.info("Cohort deleted with ID: {}", id);
            return ResponseEntity.ok(deleted);
        } catch (RuntimeException ex) {
            log.error("Cohort not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cohort not found: " + ex.getMessage());
        }
    }

    @GetMapping("/searchByCohort")
    public ResponseEntity<?> getUsersByCohort(@RequestParam Long id) {
        try {
            List<UserDTO> users = cohortService.getUsersByCohortId(id);
            log.info("Retrieved {} users for cohort ID: {}", users.size(), id);
            return ResponseEntity.ok(users);
        } catch (RuntimeException ex) {
            log.error("Cohort not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cohort not found: " + ex.getMessage());
        }
    }

    @GetMapping("/searchByProject")
    public ResponseEntity<?> getCohortsByProject(@RequestParam Long projectId) {
        try {
            List<CohortDTO> cohorts = cohortService.searchByProject(projectId);
            log.info("Retrieved {} cohorts for project ID: {}", cohorts.size(), projectId);
            return ResponseEntity.ok(cohorts);
        } catch (RuntimeException ex) {
            log.error("Project not found with ID: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project not found: " + ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCohorts() {
        try {
            List<CohortDTO> cohorts = cohortService.getAllCohorts();
            log.info("Retrieved {} cohorts", cohorts.size());
            return ResponseEntity.ok(cohorts);
        } catch (Exception ex) {
            log.error("Unexpected error retrieving all cohorts", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllCohorts() {
        try {
            cohortService.deleteAllCohorts();
            log.info("All cohorts deleted successfully");
            return ResponseEntity.ok("All cohorts deleted successfully");
        } catch (Exception ex) {
            log.error("Failed to delete all cohorts", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: Failed to delete cohorts");
        }
    }

    @GetMapping("/getOne")
    public ResponseEntity<?> getOneCohort(@RequestParam Long id) {
        try {
            CohortDTO cohort = cohortService.getOneCohort(id);
            log.info("Retrieved cohort with ID: {}", id);
            return ResponseEntity.ok(cohort);
        } catch (RuntimeException ex) {
            log.error("Cohort not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cohort not found: " + ex.getMessage());
        }
    }
}