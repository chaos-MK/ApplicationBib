package com.bib.app.controller;

import com.bib.app.service.ICohortService;
import com.bib.app.resolver.CohortResolver;
import com.bib.app.dto.CohortDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bib.app.entities.Cohort;
import com.bib.app.entities.User;
import java.util.List;

@RestController
@RequestMapping("/cohort")
public class CohortController {

    @Autowired
    private ICohortService cohortService;

    @Autowired
    private CohortResolver cohortResolver;

    @PostMapping("/addCohort")
    public Cohort add(@RequestBody Cohort cohort){
        return cohortService.add(cohort);
    }

    @DeleteMapping("/cohortdelete/{id}")
    public ResponseEntity<?> deleteCohort(@PathVariable Long id) {
        Cohort deleted = cohortService.Deleteone(id);
        if (deleted != null) {
            return ResponseEntity.ok(deleted);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cohort not found");
        }
    }

    @GetMapping("/searchByCohort")
    public ResponseEntity<List<User>> getUsersByCohort(@RequestParam Long id) {
        List<User> users = cohortService.getUsersByCohortId(id);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/searchByProject")
    public ResponseEntity<List<CohortDTO>> getCohortsByProject(@RequestParam Long projectId) {
        List<Cohort> cohorts = cohortService.searchByProject(projectId);
        List<CohortDTO> cohortDTOs = cohortResolver.toCohortDTOList(cohorts);
        return ResponseEntity.ok(cohortDTOs);
    }

    @GetMapping
    public ResponseEntity<List<Cohort>> getAllCohort() {
        return ResponseEntity.ok(cohortService.getAllCohort());
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllCohorts() {
        cohortService.deleteAllCohorts();
        return ResponseEntity.ok("All cohorts deleted successfully.");
    }

    @GetMapping("/getOne")
    public ResponseEntity<CohortDTO> getOneCohort(@RequestParam Long id) {
        Cohort cohort = cohortService.getOneCohort(id);
        CohortDTO cohortDTO = cohortResolver.toCohortDTO(cohort);
        return ResponseEntity.ok(cohortDTO);
    }
}