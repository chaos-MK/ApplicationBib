package com.bib.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CohortDTO {
    private Long id;
    private String cohortName;
    private String version; // Changed from softwareVersion to match frontend
    private String startDate;
    private String endDate;
    private String duration;
    private String overallStatus;
    private Boolean hasGraphs;
    private Boolean hasDashboard;
    private Long project_id; // Changed from projectId to match frontend
    private List<UserDTO> users;
    private List<StageDTO> stages;
    
    // New statistics fields
    private StatisticsDTO statistics;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCohortName() { return cohortName; }
    public void setCohortName(String cohortName) { this.cohortName = cohortName; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

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

    public Long getProject_id() { return project_id; }
    public void setProject_id(Long project_id) { this.project_id = project_id; }

    public List<UserDTO> getUsers() { return users; }
    public void setUsers(List<UserDTO> users) { this.users = users; }

    public List<StageDTO> getStages() { return stages; }
    public void setStages(List<StageDTO> stages) { this.stages = stages; }

    public StatisticsDTO getStatistics() { return statistics; }
    public void setStatistics(StatisticsDTO statistics) { this.statistics = statistics; }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticsDTO {
        private Long totalUsers;
        private Long activeUsers;
        private Long totalSessions;
        private Long totalStages;
        private Long inactiveUsers;
        private Double activeUserPercentage;
        private List<SessionSummaryDTO> sessionSummaries;
        private List<StageSummaryDTO> stageSummaries;

        public Long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

        public Long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }

        public Long getTotalSessions() { return totalSessions; }
        public void setTotalSessions(Long totalSessions) { this.totalSessions = totalSessions; }

        public Long getTotalStages() { return totalStages; }
        public void setTotalStages(Long totalStages) { this.totalStages = totalStages; }

        public Long getInactiveUsers() { return inactiveUsers; }
        public void setInactiveUsers(Long inactiveUsers) { this.inactiveUsers = inactiveUsers; }

        public Double getActiveUserPercentage() { return activeUserPercentage; }
        public void setActiveUserPercentage(Double activeUserPercentage) { this.activeUserPercentage = activeUserPercentage; }

        public List<SessionSummaryDTO> getSessionSummaries() { return sessionSummaries; }
        public void setSessionSummaries(List<SessionSummaryDTO> sessionSummaries) { this.sessionSummaries = sessionSummaries; }

        public List<StageSummaryDTO> getStageSummaries() { return stageSummaries; }
        public void setStageSummaries(List<StageSummaryDTO> stageSummaries) { this.stageSummaries = stageSummaries; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionSummaryDTO {
        private Long sessionId;
        private String sessionName;
        private String status;
        private String userName;
        private Long stageCount;
        private Boolean hasGraphs;
        private Boolean hasDashboard;

        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

        public String getSessionName() { return sessionName; }
        public void setSessionName(String sessionName) { this.sessionName = sessionName; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public Long getStageCount() { return stageCount; }
        public void setStageCount(Long stageCount) { this.stageCount = stageCount; }

        public Boolean getHasGraphs() { return hasGraphs; }
        public void setHasGraphs(Boolean hasGraphs) { this.hasGraphs = hasGraphs; }

        public Boolean getHasDashboard() { return hasDashboard; }
        public void setHasDashboard(Boolean hasDashboard) { this.hasDashboard = hasDashboard; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageSummaryDTO {
        private Long stageId;
        private String stageName;
        private String status;
        private Integer currentUsers;
        private Integer enteringUsers;
        private Integer exitingUsers;
        private String currentPct;
        private String exitingPct;
        private String associatedWith; // "USER", "SESSION", "COHORT", "PROJECT"
        private Long associatedId;

        public Long getStageId() { return stageId; }
        public void setStageId(Long stageId) { this.stageId = stageId; }

        public String getStageName() { return stageName; }
        public void setStageName(String stageName) { this.stageName = stageName; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Integer getCurrentUsers() { return currentUsers; }
        public void setCurrentUsers(Integer currentUsers) { this.currentUsers = currentUsers; }

        public Integer getEnteringUsers() { return enteringUsers; }
        public void setEnteringUsers(Integer enteringUsers) { this.enteringUsers = enteringUsers; }

        public Integer getExitingUsers() { return exitingUsers; }
        public void setExitingUsers(Integer exitingUsers) { this.exitingUsers = exitingUsers; }

        public String getCurrentPct() { return currentPct; }
        public void setCurrentPct(String currentPct) { this.currentPct = currentPct; }

        public String getExitingPct() { return exitingPct; }
        public void setExitingPct(String exitingPct) { this.exitingPct = exitingPct; }

        public String getAssociatedWith() { return associatedWith; }
        public void setAssociatedWith(String associatedWith) { this.associatedWith = associatedWith; }

        public Long getAssociatedId() { return associatedId; }
        public void setAssociatedId(Long associatedId) { this.associatedId = associatedId; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String apUid;
        private String email;
        private String fullName;
        private String creationTime;
        private List<String> statuses;
        private List<String> privileges;
        private String reactivationToken;
        private boolean disabled;
        private String displayName;
        private boolean superUser;
        private UserContextDTO userContext;
        
        // Add these new fields for session and stage counts
        private Long sessionCount;
        private Long stageCount;
        
        
        // Getters and Setters for all fields including the new ones
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getApUid() { return apUid; }
        public void setApUid(String apUid) { this.apUid = apUid; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getCreationTime() { return creationTime; }
        public void setCreationTime(String creationTime) { this.creationTime = creationTime; }
        
        public List<String> getStatuses() { return statuses; }
        public void setStatuses(List<String> statuses) { this.statuses = statuses; }
        
        public List<String> getPrivileges() { return privileges; }
        public void setPrivileges(List<String> privileges) { this.privileges = privileges; }
        
        public String getReactivationToken() { return reactivationToken; }
        public void setReactivationToken(String reactivationToken) { this.reactivationToken = reactivationToken; }
        
        public boolean isDisabled() { return disabled; }
        public void setDisabled(boolean disabled) { this.disabled = disabled; }
        
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        
        public boolean isSuperUser() { return superUser; }
        public void setSuperUser(boolean superUser) { this.superUser = superUser; }
        
        public UserContextDTO getUserContext() { return userContext; }
        public void setUserContext(UserContextDTO userContext) { this.userContext = userContext; }
        
        // New getters and setters for session and stage counts
        public Long getSessionCount() { return sessionCount; }
        public void setSessionCount(Long sessionCount) { this.sessionCount = sessionCount; }
        
        public Long getStageCount() { return stageCount; }
        public void setStageCount(Long stageCount) { this.stageCount = stageCount; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrivilegeDTO {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserContextDTO {
        private Boolean hasLiked;
        private Boolean isFollowing;
        private Boolean isInFavorites;
        private Boolean owns;
        private Boolean isOwnerBlocked;
        private Boolean isOwnerMuted;

        public Boolean getHasLiked() { return hasLiked; }
        public void setHasLiked(Boolean hasLiked) { this.hasLiked = hasLiked; }

        public Boolean getIsFollowing() { return isFollowing; }
        public void setIsFollowing(Boolean isFollowing) { this.isFollowing = isFollowing; }

        public Boolean getIsInFavorites() { return isInFavorites; }
        public void setIsInFavorites(Boolean isInFavorites) { this.isInFavorites = isInFavorites; }

        public Boolean getOwns() { return owns; }
        public void setOwns(Boolean owns) { this.owns = owns; }

        public Boolean getIsOwnerBlocked() { return isOwnerBlocked; }
        public void setIsOwnerBlocked(Boolean isOwnerBlocked) { this.isOwnerBlocked = isOwnerBlocked; }

        public Boolean getIsOwnerMuted() { return isOwnerMuted; }
        public void setIsOwnerMuted(Boolean isOwnerMuted) { this.isOwnerMuted = isOwnerMuted; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageDTO {
        private Long id;
        private String type;
        private String state;
        private StageStatsDTO stageStats;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public StageStatsDTO getStageStats() { return stageStats; }
        public void setStageStats(StageStatsDTO stageStats) { this.stageStats = stageStats; }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageStatsDTO {
        private Long id;
        private Long cohortID;
        private Integer peopleEntered;
        private Integer peopleInactive;
        private Integer peopleExit;
        private Integer overall;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getCohortID() { return cohortID; }
        public void setCohortID(Long cohortID) { this.cohortID = cohortID; }

        public Integer getPeopleEntered() { return peopleEntered; }
        public void setPeopleEntered(Integer peopleEntered) { this.peopleEntered = peopleEntered; }

        public Integer getPeopleInactive() { return peopleInactive; }
        public void setPeopleInactive(Integer peopleInactive) { this.peopleInactive = peopleInactive; }

        public Integer getPeopleExit() { return peopleExit; }
        public void setPeopleExit(Integer peopleExit) { this.peopleExit = peopleExit; }

        public Integer getOverall() { return overall; }
        public void setOverall(Integer overall) { this.overall = overall; }
    }
}