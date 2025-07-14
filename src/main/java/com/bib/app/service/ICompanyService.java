package com.bib.app.service;

import com.bib.app.entities.Company;

import java.util.List;

public interface ICompanyService {
    Company add(Company company);
    Company deleteOne(Long id);
    void deleteAllCompanies();
    Company getOneCompany(Long id);
    List<Company> getAllCompanies();
}