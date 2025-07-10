package com.bib.app.entities;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;
    
    private String companyName;
    private String contactInfoLink;
    private Integer current;
    private Integer entering;
    private Integer exiting;
    private String currentPct;
    private String exitingPct;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("company-projects")
    private List<Project> projects;
    
    public Company() {}
    
    public Company(String companyName, String contactInfoLink) {
        this.companyName = companyName;
        this.contactInfoLink = contactInfoLink;
    }
    
    // All getters and setters (same as before)
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getContactInfoLink() { return contactInfoLink; }
    public void setContactInfoLink(String contactInfoLink) { this.contactInfoLink = contactInfoLink; }
    
    public Integer getCurrent() { return current; }
    public void setCurrent(Integer current) { this.current = current; }
    
    public Integer getEntering() { return entering; }
    public void setEntering(Integer entering) { this.entering = entering; }
    
    public Integer getExiting() { return exiting; }
    public void setExiting(Integer exiting) { this.exiting = exiting; }
    
    public String getCurrentPct() { return currentPct; }
    public void setCurrentPct(String currentPct) { this.currentPct = currentPct; }
    
    public String getExitingPct() { return exitingPct; }
    public void setExitingPct(String exitingPct) { this.exitingPct = exitingPct; }
    
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
}