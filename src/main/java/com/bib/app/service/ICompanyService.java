package com.bib.app.service;

import com.bib.app.entities.Company;

import java.util.List;

public interface ICompanyService {
    Company add(Company company);
    Company Delete(Long CompanyID);
    void deleteAllCompany();
    Company getOneCompany (Long CompanyID) ;
    List<Company> getAllCompany() ;
}