package com.bib.app.controller;

import com.bib.app.dto.CompanyCreateDTO;
import com.bib.app.dto.CompanyDTO;
import com.bib.app.entities.Company;
import com.bib.app.resolver.CompanyResolver;
import com.bib.app.service.ICompanyService;
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
@RequestMapping(value = "/company", produces = "application/json; charset=UTF-8")
@RequiredArgsConstructor
public class CompanyController {
    private final ICompanyService companyService;
    private final CompanyResolver companyResolver;

    @PostMapping
    public ResponseEntity<?> createCompany(@Valid @RequestBody CompanyCreateDTO companyDTO) {
        try {
            Company company = companyResolver.convertToEntity(companyDTO);
            Company savedCompany = companyService.add(company);
            log.info("Company created with ID: {}", savedCompany.getCompanyId());
            return ResponseEntity
                    .created(URI.create("/company/" + savedCompany.getCompanyId()))
                    .body(companyResolver.convertToDTO(savedCompany));
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for creating company: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error creating company", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        try {
            Company deleted = companyService.deleteOne(id);
            log.info("Company deleted with ID: {}", id);
            return ResponseEntity.ok(companyResolver.convertToDTO(deleted));
        } catch (RuntimeException e) {
            log.error("Company not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Company not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error deleting company", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllCompanies() {
        try {
            companyService.deleteAllCompanies();
            log.info("All companies deleted successfully");
            return ResponseEntity.ok("All companies deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete all companies", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: Failed to delete companies");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCompany(@PathVariable Long id) {
        try {
            Company company = companyService.getOneCompany(id);
            log.info("Retrieved company with ID: {}", id);
            return ResponseEntity.ok(companyResolver.convertToDTO(company));
        } catch (RuntimeException e) {
            log.error("Company not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Company not found: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCompanies() {
        try {
            List<Company> companies = companyService.getAllCompanies();
            log.info("Retrieved {} companies", companies.size());
            return ResponseEntity.ok(companyResolver.convertToDTO(companies));
        } catch (Exception e) {
            log.error("Unexpected error retrieving companies", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: An unexpected error occurred");
        }
    }
}