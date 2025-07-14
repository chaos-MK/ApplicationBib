package com.bib.app.service;


import com.bib.app.entities.Company;
import com.bib.app.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService implements ICompanyService{
    private final CompanyRepository companyRepository;
    

    @Override
    public Company add(Company company) {
        return this.companyRepository.save(company);
    }

    @Override
    public Company deleteOne(Long CompanyID) {
        Company company = companyRepository.findById(CompanyID)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + CompanyID));

        companyRepository.deleteById(CompanyID);
        return company;
    }

    @Override
    public void deleteAllCompanies() {
        companyRepository.deleteAll();
    }

    @Override
    public Company getOneCompany(Long CompanyID) {
        return companyRepository.findById(CompanyID)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + CompanyID));
    }

    @Override
    public List<Company> getAllCompanies() {
        return (List<Company>) companyRepository.findAll();
    }
}
