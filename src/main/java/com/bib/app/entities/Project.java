package com.bib.app.entities;

import java.time.LocalDate;
import java.util.List;
import jakarta.persistence.CascadeType;
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
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String projectName;
    private String projectWebsite;
    private LocalDate startDate;
    private LocalDate endDate;
    private String duration;
    private String overallStatus;
    private Boolean hasGraphs;
    private Boolean hasDashboard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonBackReference("company-projects")
    private Company company;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("project-cohorts")
    private List<Cohort> cohorts;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("project-stages")
    private List<Stage> stages;

    public Project() {}

    public Project(String projectName, String projectWebsite, LocalDate startDate, LocalDate endDate, String duration, String overallStatus) {
        this.projectName = projectName;
        this.projectWebsite = projectWebsite;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.overallStatus = overallStatus;
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

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public List<Cohort> getCohorts() { return cohorts; }
    public void setCohorts(List<Cohort> cohorts) { this.cohorts = cohorts; }

    public List<Stage> getStages() { return stages; }
    public void setStages(List<Stage> stages) { this.stages = stages; }

    public boolean isHasGraphs() {
        return hasGraphs != null && hasGraphs;
    }
}