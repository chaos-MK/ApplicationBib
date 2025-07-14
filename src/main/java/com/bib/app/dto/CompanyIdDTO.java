package com.bib.app.dto;

import jakarta.validation.constraints.NotNull;

public class CompanyIdDTO {
    @NotNull(message = "Company ID is required")
    private Long companyId;

    // Getters and Setters
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
}