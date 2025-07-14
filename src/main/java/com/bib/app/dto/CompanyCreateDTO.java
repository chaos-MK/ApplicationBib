package com.bib.app.dto;

import com.bib.app.dto.ProjectDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;

import java.util.List;

public class CompanyCreateDTO {
    private String companyId;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String contactInfoLink;
    private Integer current;
    private Integer entering;
    private Integer exiting;
    private String currentPct;
    private String exitingPct;

    @Valid
    private List<ProjectDTO> projects;

    // Default constructor
    public CompanyCreateDTO() {}

    // Constructor with all fields
    public CompanyCreateDTO(String companyId, String companyName, String contactInfoLink,
                            Integer current, Integer entering, Integer exiting,
                            String currentPct, String exitingPct, List<ProjectDTO> projects) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.contactInfoLink = contactInfoLink;
        this.current = current;
        this.entering = entering;
        this.exiting = exiting;
        this.currentPct = currentPct;
        this.exitingPct = exitingPct;
        this.projects = projects;
    }

    // Getters and Setters
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactInfoLink() {
        return contactInfoLink;
    }

    public void setContactInfoLink(String contactInfoLink) {
        this.contactInfoLink = contactInfoLink;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getEntering() {
        return entering;
    }

    public void setEntering(Integer entering) {
        this.entering = entering;
    }

    public Integer getExiting() {
        return exiting;
    }

    public void setExiting(Integer exiting) {
        this.exiting = exiting;
    }

    public String getCurrentPct() {
        return currentPct;
    }

    public void setCurrentPct(String currentPct) {
        this.currentPct = currentPct;
    }

    public String getExitingPct() {
        return exitingPct;
    }

    public void setExitingPct(String exitingPct) {
        this.exitingPct = exitingPct;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "CompanyCreateDTO{" +
                "companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", contactInfoLink='" + contactInfoLink + '\'' +
                ", current=" + current +
                ", entering=" + entering +
                ", exiting=" + exiting +
                ", currentPct='" + currentPct + '\'' +
                ", exitingPct='" + exitingPct + '\'' +
                ", projects=" + projects +
                '}';
    }
}