package com.bib.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProjectCreateDTO {
    @NotBlank(message = "Project name is required")
    private String projectName;

    private String projectWebsite;

    @NotBlank(message = "Start date is required")
    private String startDate;

    private String endDate;

    private String duration;

    @NotBlank(message = "Overall status is required")
    private String overallStatus;

    private boolean hasGraphs;
    private boolean hasDashboard;

    @NotNull(message = "Company is required")
    private CompanyIdDTO company;

    // Getters and Setters
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectWebsite() { return projectWebsite; }
    public void setProjectWebsite(String projectWebsite) { this.projectWebsite = projectWebsite; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

    public boolean isHasGraphs() { return hasGraphs; }
    public void setHasGraphs(boolean hasGraphs) { this.hasGraphs = hasGraphs; }

    public boolean isHasDashboard() { return hasDashboard; }
    public void setHasDashboard(boolean hasDashboard) { this.hasDashboard = hasDashboard; }

    public CompanyIdDTO getCompany() { return company; }
    public void setCompany(CompanyIdDTO company) { this.company = company; }
}