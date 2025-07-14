package com.bib.app.dto;

public class CohortDTO {
    private Long cohortId;
    private String cohortName;
    private String softwareVersion;
    private String startDate; // ISO date string for frontend
    private String endDate;   // ISO date string for frontend
    private String duration;
    private String overallStatus;
    private Boolean hasGraphs;
    private Boolean hasDashboard;
    private Long projectId;
    private String projectName;
    private int userCount;    // Number of users in the cohort
    private int stageCount;   // Number of stages in the cohort

    // Constructors
    public CohortDTO() {}

    public CohortDTO(Long cohortId, String cohortName, String softwareVersion, String startDate, String endDate,
                     String duration, String overallStatus, Boolean hasGraphs, Boolean hasDashboard,
                     Long projectId, String projectName, int userCount, int stageCount) {
        this.cohortId = cohortId;
        this.cohortName = cohortName;
        this.softwareVersion = softwareVersion;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.overallStatus = overallStatus;
        this.hasGraphs = hasGraphs;
        this.hasDashboard = hasDashboard;
        this.projectId = projectId;
        this.projectName = projectName;
        this.userCount = userCount;
        this.stageCount = stageCount;
    }

    // Getters and Setters
    public Long getCohortId() { return cohortId; }
    public void setCohortId(Long cohortId) { this.cohortId = cohortId; }

    public String getCohortName() { return cohortName; }
    public void setCohortName(String cohortName) { this.cohortName = cohortName; }

    public String getSoftwareVersion() { return softwareVersion; }
    public void setSoftwareVersion(String softwareVersion) { this.softwareVersion = softwareVersion; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

    public Boolean getHasGraphs() { return hasGraphs; }
    public void setHasGraphs(Boolean hasGraphs) { this.hasGraphs = hasGraphs; }

    public Boolean getHasDashboard() { return hasDashboard; }
    public void setHasDashboard(Boolean hasDashboard) { this.hasDashboard = hasDashboard; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public int getUserCount() { return userCount; }
    public void setUserCount(int userCount) { this.userCount = userCount; }

    public int getStageCount() { return stageCount; }
    public void setStageCount(int stageCount) { this.stageCount = stageCount; }
}