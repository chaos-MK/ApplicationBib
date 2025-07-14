package com.bib.app.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;
    private LocalDate startDate;
    private String status;
    private Boolean hasGraphs;
    private Boolean hasDashboard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id")
    @JsonBackReference("cohort-users")
    private Cohort cohort;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-sessions")
    private List<Session> sessions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("user-stages")
    private List<Stage> stages;

    public User() {}

    public User(String userName, LocalDate startDate, String status) {
        this.userName = userName;
        this.startDate = startDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getHasGraphs() { return hasGraphs; }
    public void setHasGraphs(Boolean hasGraphs) { this.hasGraphs = hasGraphs; }

    public Boolean getHasDashboard() { return hasDashboard; }
    public void setHasDashboard(Boolean hasDashboard) { this.hasDashboard = hasDashboard; }

    public Cohort getCohort() { return cohort; }
    public void setCohort(Cohort cohort) { this.cohort = cohort; }

    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }

    public List<Stage> getStages() { return stages; }
    public void setStages(List<Stage> stages) { this.stages = stages; }
}