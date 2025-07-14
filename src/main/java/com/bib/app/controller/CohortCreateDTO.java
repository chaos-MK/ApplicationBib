package com.bib.app.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CohortCreateDTO {
    @NotBlank(message = "Cohort name is required")
    private String cohortName;

    @NotBlank(message = "Software version is required")
    private String softwareVersion;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Duration is required")
    private String duration;

    @NotBlank(message = "State is required")
    private String state;

    private boolean hasGraphs;
    private boolean hasDashboard;

    @NotNull(message = "Project is required")
    private ProjectIdDTO project;

    // Getters and Setters
    public String getCohortName() { return cohortName; }
    public void setCohortName(String cohortName) { this.cohortName = cohortName; }

    public String getSoftwareVersion() { return softwareVersion; }
    public void setSoftwareVersion(String softwareVersion) { this.softwareVersion = softwareVersion; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public boolean isHasGraphs() { return hasGraphs; }
    public void setHasGraphs(boolean hasGraphs) { this.hasGraphs = hasGraphs; }

    public boolean isHasDashboard() { return hasDashboard; }
    public void setHasDashboard(boolean hasDashboard) { this.hasDashboard = hasDashboard; }

    public ProjectIdDTO getProject() { return project; }
    public void setProject(ProjectIdDTO project) { this.project = project; }
}