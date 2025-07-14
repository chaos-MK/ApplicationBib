package com.bib.app.resolver;

import com.bib.app.dto.CompanyCreateDTO;
import com.bib.app.dto.CompanyDTO;
import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Company;
import com.bib.app.entities.Project;
import com.bib.app.repository.CompanyRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompanyResolver {

    private final ProjectResolver projectResolver;
    private final CompanyRepository companyRepository;

    public CompanyDTO convertToDTO(Company company) {
        if (company == null) {
            return null;
        }

        // Initialize lazy-loaded projects
        Hibernate.initialize(company.getProjects());
        List<ProjectDTO> projectDTOs = projectResolver.convertToDTO(company.getProjects());

        return new CompanyDTO(
                String.valueOf(company.getCompanyId()),
                company.getCompanyName(),
                company.getContactInfoLink(),
                company.getCurrent(),
                company.getEntering(),
                company.getExiting(),
                company.getCurrentPct(),
                company.getExitingPct(),
                projectDTOs
        );
    }

    public List<CompanyDTO> convertToDTO(List<Company> companies) {
        if (companies == null || companies.isEmpty()) {
            return List.of();
        }

        // Process the provided companies list directly
        return companies.stream()
                .map(company -> {
                    // Initialize lazy-loaded projects for each company
                    Hibernate.initialize(company.getProjects());
                    return convertToDTO(company);
                })
                .collect(Collectors.toList());
    }

    public Company convertToEntity(@Valid CompanyCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Company company = new Company();
        if (dto.getCompanyId() != null) {
            company.setCompanyId(Long.valueOf(dto.getCompanyId()));
        }
        company.setCompanyName(dto.getCompanyName());
        company.setContactInfoLink(dto.getContactInfoLink());
        company.setCurrent(dto.getCurrent());
        company.setEntering(dto.getEntering());
        company.setExiting(dto.getExiting());
        company.setCurrentPct(dto.getCurrentPct());
        company.setExitingPct(dto.getExitingPct());

        // Convert projects if needed
        List<Project> projects = dto.getProjects() != null
                ? dto.getProjects().stream()
                .map(projectResolver::convertToEntity)
                .collect(Collectors.toList())
                : new ArrayList<>();
        company.setProjects(projects);

        return company;
    }
}