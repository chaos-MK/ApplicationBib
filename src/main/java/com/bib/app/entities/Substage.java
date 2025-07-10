package com.bib.app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "substages")
public class Substage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long substageId;
    
    private String name;
    private String status;
    private Integer current;
    private Integer entering;
    private Integer exiting;
    private String currentPct;
    private String exitingPct;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id")
    @JsonBackReference("stage-substages")
    private Stage stage;
    
    public Substage() {}
    
    public Substage(String name, String status) {
        this.name = name;
        this.status = status;
    }
    
    // All getters and setters
    public Long getSubstageId() { return substageId; }
    public void setSubstageId(Long substageId) { this.substageId = substageId; }
    
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
    
    public Stage getStage() { return stage; }
    public void setStage(Stage stage) { this.stage = stage; }
}