package com.bib.app.entities;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "cohorts")
public class Cohort {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cohortId;
    
    @Column(nullable = false)
    private String cohortName;
    
    @Column(nullable = false)
    private String softwareVersion;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Column(nullable = false)
    private String duration;
    
    @Column(nullable = false)
    private String overallStatus;
    
    @Column(nullable = false)
    private Boolean hasGraphs;
    
    @Column(nullable = false)
    private Boolean hasDashboard;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonBackReference("project-cohorts")
    private Project project;
    
    @OneToMany(mappedBy = "cohort", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("cohort-users")
    private List<User> users;
    
    @OneToMany(mappedBy = "cohort", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("cohort-stages")
    private List<Stage> stages;
    
    public Cohort() {}
    
    public Cohort(String cohortName, String softwareVersion, LocalDate startDate, 
                 LocalDate endDate, String duration, String overallStatus, 
                 Boolean hasGraphs, Boolean hasDashboard) {
        this.cohortName = cohortName;
        this.softwareVersion = softwareVersion;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.overallStatus = overallStatus;
        this.hasGraphs = hasGraphs;
        this.hasDashboard = hasDashboard;
    }
    
    // All getters and setters
    public Long getCohortId() { return cohortId; }
    public void setCohortId(Long cohortId) { this.cohortId = cohortId; }
    
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
    
    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }
    
    public Boolean getHasGraphs() { return hasGraphs; }
    public void setHasGraphs(Boolean hasGraphs) { this.hasGraphs = hasGraphs; }
    
    public Boolean getHasDashboard() { return hasDashboard; }
    public void setHasDashboard(Boolean hasDashboard) { this.hasDashboard = hasDashboard; }
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
    
    public List<Stage> getStages() { return stages; }
    public void setStages(List<Stage> stages) { this.stages = stages; }
}