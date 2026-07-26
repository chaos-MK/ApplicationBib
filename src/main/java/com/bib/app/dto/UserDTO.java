package com.bib.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;


public class UserDTO {
    private Long userId;
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    private String status;
    private Boolean hasGraphs;
    private Boolean hasDashboard;
    private Long cohortId; // Reference to the associated cohort
    private String cohortName; // Optional for display purposes

    // Constructors
    public UserDTO() {}

    public UserDTO(Long userId, String userName, String startDate, String status,
                   Boolean hasGraphs, Boolean hasDashboard, Long cohortId, String cohortName) {
        this.userId = userId;
        this.userName = userName;
        this.startDate = startDate;
        this.status = status;
        this.hasGraphs = hasGraphs;
        this.hasDashboard = hasDashboard;
        this.cohortId = cohortId;
        this.cohortName = cohortName;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getHasGraphs() { return hasGraphs; }
    public void setHasGraphs(Boolean hasGraphs) { this.hasGraphs = hasGraphs; }

    public Boolean getHasDashboard() { return hasDashboard; }
    public void setHasDashboard(Boolean hasDashboard) { this.hasDashboard = hasDashboard; }

    public Long getCohortId() { return cohortId; }
    public void setCohortId(Long cohortId) { this.cohortId = cohortId; }

    public String getCohortName() { return cohortName; }
    public void setCohortName(String cohortName) { this.cohortName = cohortName; }
}