package com.bib.app.dto;

import java.time.LocalDate;

public class ProjectDTO {
	private Long projectId;
    private String projectName;
    private String projectWebsite;
    private LocalDate startDate;
    private LocalDate endDate;
    private String duration;
    private String overallStatus;
    private Boolean hasGraphs;
    private Boolean hasDashboard;
    private Long companyId;
    private String companyName;
    private Long userCount;
    private Long sessionCount;
    private Long cohortCount;
    
 // Constructors
    public ProjectDTO() {}

    public ProjectDTO(Long projectId, String projectName, String projectWebsite,
                     LocalDate startDate, LocalDate endDate, String duration,
                     String overallStatus, Boolean hasGraphs, Boolean hasDashboard,
                     Long companyId, String companyName, Long userCount, Long sessionCount, Long cohortCount) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectWebsite = projectWebsite;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.overallStatus = overallStatus;
        this.hasGraphs = hasGraphs;
        this.hasDashboard = hasDashboard;
        this.companyId = companyId;
        this.companyName = companyName;
        this.userCount = userCount;
        this.sessionCount = sessionCount;
        this.cohortCount = cohortCount;
    }

    // Getters and Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectWebsite() { return projectWebsite; }
    public void setProjectWebsite(String projectWebsite) { this.projectWebsite = projectWebsite; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

    public Boolean getHasGraphs() { return hasGraphs; }
    public void setHasGraphs(Boolean hasGraphs) { this.hasGraphs = hasGraphs; }

    public Boolean getHasDashboard() { return hasDashboard; }
    public void setHasDashboard(Boolean hasDashboard) { this.hasDashboard = hasDashboard; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Long getUserCount() { return userCount; }
    public void setUserCount(Long userCount) { this.userCount = userCount; }

    public Long getSessionCount() { return sessionCount; }
    public void setSessionCount(Long sessionCount) { this.sessionCount = sessionCount; }

    public Long getCohortCount() { return cohortCount; }
    public void setCohortCount(Long cohortCount) { this.cohortCount = cohortCount; }
}