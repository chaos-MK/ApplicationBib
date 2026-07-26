package com.bib.app.service;


import com.bib.app.entities.Company;
import com.bib.app.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CompanyService implements ICompanyService{
    private final CompanyRepository companyRepository;
    
    public CompanyService(CompanyRepository companyRepository) {
    	this.companyRepository = companyRepository;
    }
    

    @Override
    public Company add(Company company) {
        return this.companyRepository.save(company);
    }

    @Override
    public Company Delete(Long CompanyID) {
        Company company = companyRepository.findById(CompanyID)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + CompanyID));

        companyRepository.deleteById(CompanyID);
        return company;
    }

    @Override
    public void deleteAllCompany() {
        companyRepository.deleteAll();
    }

    @Override
    public Company getOneCompany(Long CompanyID) {
        return companyRepository.findById(CompanyID)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + CompanyID));
    }

    @Override
    public List<Company> getAllCompany() {
        return (List<Company>) companyRepository.findAll();
    }
}
