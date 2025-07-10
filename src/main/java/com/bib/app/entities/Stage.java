package com.bib.app.entities;

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
@Table(name = "stages")
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stageId;
    
    private String name;
    private String status;
    private Integer current;
    private Integer entering;
    private Integer exiting;
    private String currentPct;
    private String exitingPct;
    
    // Multiple parent relationships - use different reference names
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonBackReference("project-stages")
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id")
    @JsonBackReference("cohort-stages")
    private Cohort cohort;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-stages")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    @JsonBackReference("session-stages")
    private Session session;
    
    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("stage-substages")
    private List<Substage> substages;
    
    public Stage() {}
    
    public Stage(String name, String status) {
        this.name = name;
        this.status = status;
    }
    
    // All getters and setters
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
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
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public Cohort getCohort() { return cohort; }
    public void setCohort(Cohort cohort) { this.cohort = cohort; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }
    
    public List<Substage> getSubstages() { return substages; }
    public void setSubstages(List<Substage> substages) { this.substages = substages; }
}