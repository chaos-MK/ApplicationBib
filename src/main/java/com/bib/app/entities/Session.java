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
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;
    
    private String sessionName;
    private LocalDate startDate;
    private String status;
    private Boolean hasGraphs;
    private Boolean hasDashboard;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-sessions")
    private User user;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("session-stages")
    private List<Stage> stages;
    
    public Session() {}
    
    public Session(String sessionName, LocalDate startDate, String status) {
        this.sessionName = sessionName;
        this.startDate = startDate;
        this.status = status;
    }
    
    // All getters and setters
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    
    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getHasGraphs() { return hasGraphs; }
    public void setHasGraphs(Boolean hasGraphs) { this.hasGraphs = hasGraphs; }
    
    public Boolean getHasDashboard() { return hasDashboard; }
    public void setHasDashboard(Boolean hasDashboard) { this.hasDashboard = hasDashboard; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public List<Stage> getStages() { return stages; }
    public void setStages(List<Stage> stages) { this.stages = stages; }
}