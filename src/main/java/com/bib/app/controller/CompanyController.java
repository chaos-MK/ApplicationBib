package com.bib.app.controller;


import com.bib.app.dto.CompanyDTO;
import com.bib.app.entities.Company;
import com.bib.app.service.ICompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {
	
	 @Autowired
	 private ICompanyService companyService;
	    
	 @PostMapping("/addCompany")
	 public Company add(@RequestBody Company company){
		 return companyService.add(company);
	 }
	 
	 @DeleteMapping("/CompanyDelete/{id}")
	    public ResponseEntity<?> Delete(@PathVariable Long id) {
	        Company deleted = companyService.Delete(id);
	        if (deleted != null) {
	            return ResponseEntity.ok(deleted);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found");
	        }
	 }
	 
	 @DeleteMapping("/deleteAll")
	    public ResponseEntity<String> deleteAllCompanies() {
	        companyService.deleteAllCompany();
	        return ResponseEntity.ok("All companies deleted successfully.");
	 }
	 
	 @GetMapping("/getOne")
	    public ResponseEntity<Company> getOneCompany(@RequestParam Long id) {
	        Company company = companyService.getOneCompany(id);
	        return ResponseEntity.ok(company);
	 }
    

	 @GetMapping
	    public ResponseEntity<List<Company>> getAllCompanies() {
	        return ResponseEntity.ok(companyService.getAllCompany());
	 }
    
}