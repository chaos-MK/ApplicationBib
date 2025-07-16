package com.bib.app.resolver;

import com.bib.app.dto.CohortDTO;
import com.bib.app.dto.ProjectDTO;
import com.bib.app.entities.Cohort;
import com.bib.app.entities.Project;
import com.bib.app.entities.User;
import com.bib.app.entities.Stage;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Collections;

@Component
public class CohortResolver {

    public CohortDTO toCohortDTO(Cohort cohort) {
        if (cohort == null) {
            return null;
        }

        CohortDTO dto = new CohortDTO();
        dto.setId(cohort.getCohortId());
        dto.setCohortName(cohort.getCohortName());
        dto.setStartDate(cohort.getStartDate() != null ? cohort.getStartDate().toString() : null);
        dto.setEndDate(cohort.getEndDate() != null ? cohort.getEndDate().toString() : null);
        dto.setVersion(cohort.getSoftwareVersion()); // Map to version
        dto.setDuration(cohort.getDuration());
        dto.setOverallStatus(cohort.getOverallStatus());
        dto.setHasGraphs(cohort.getHasGraphs());
        dto.setHasDashboard(cohort.getHasDashboard());

        // Set project ID from the relationship
        if (cohort.getProject() != null) {
            dto.setProject_id(cohort.getProject().getProjectId());
        }

        // Convert users - with null checks and lazy loading handling
        try {
            if (cohort.getUsers() != null && !cohort.getUsers().isEmpty()) {
                dto.setUsers(cohort.getUsers().stream()
                    .map(this::toUserDTO)
                    .collect(Collectors.toList()));
            } else {
                dto.setUsers(Collections.emptyList());
            }
        } catch (Exception e) {
            // Handle lazy loading exceptions
            dto.setUsers(Collections.emptyList());
        }

        // Convert stages - with null checks and lazy loading handling
        try {
            if (cohort.getStages() != null && !cohort.getStages().isEmpty()) {
                dto.setStages(cohort.getStages().stream()
                    .map(this::toStageDTO)
                    .collect(Collectors.toList()));
            } else {
                dto.setStages(Collections.emptyList());
            }
        } catch (Exception e) {
            // Handle lazy loading exceptions
            dto.setStages(Collections.emptyList());
        }

        // Calculate and set statistics
        dto.setStatistics(calculateStatistics(cohort));

        return dto;
    }

    public List<CohortDTO> toCohortDTOList(List<Cohort> cohorts) {
        if (cohorts == null) {
            return Collections.emptyList();
        }

        return cohorts.stream()
            .map(this::toCohortDTO)
            .collect(Collectors.toList());
    }

    public ProjectDTO toProjectDTO(Project project) {
        if (project == null) {
            return null;
        }

        ProjectDTO dto = new ProjectDTO();
        dto.setProjectId(project.getProjectId());
        dto.setProjectName(project.getProjectName());

        return dto;
    }

    private CohortDTO.UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        CohortDTO.UserDTO dto = new CohortDTO.UserDTO();
        dto.setId(user.getUserId());
        dto.setApUid(user.getUserName()); // Map userName to apUid for now
        dto.setEmail(user.getUserName() + "@example.com"); // Generate email or get from user entity
        dto.setFullName(user.getUserName());
        dto.setCreationTime(user.getStartDate() != null ? user.getStartDate().toString() : null);
        
        // Convert single status to array of statuses
        if (user.getStatus() != null && !user.getStatus().isEmpty()) {
            dto.setStatuses(Arrays.asList(user.getStatus()));
        } else {
            dto.setStatuses(Collections.emptyList());
        }

        // Set default privileges (or get from user entity if available)
        dto.setPrivileges(Collections.emptyList());
        
        dto.setReactivationToken("");
        dto.setDisabled(false); // Set based on user status or add to entity
        dto.setDisplayName(user.getUserName());
        dto.setSuperUser(false); // Set based on user role or add to entity
        
        // Calculate session count and stage count
        try {
            dto.setSessionCount(user.getSessions() != null ? (long) user.getSessions().size() : 0L);
        } catch (Exception e) {
            dto.setSessionCount(0L);
        }
        
        // For stage count, you might need to calculate based on your business logic
        dto.setStageCount(0L); // Set this based on your requirements
        
        // Create default user context
        CohortDTO.UserContextDTO userContext = new CohortDTO.UserContextDTO();
        userContext.setHasLiked(false);
        userContext.setIsFollowing(false);
        userContext.setIsInFavorites(false);
        userContext.setOwns(false);
        userContext.setIsOwnerBlocked(false);
        userContext.setIsOwnerMuted(false);
        dto.setUserContext(userContext);

        return dto;
    }

    private CohortDTO.StageDTO toStageDTO(Stage stage) {
        if (stage == null) {
            return null;
        }

        CohortDTO.StageDTO dto = new CohortDTO.StageDTO();
        dto.setId(stage.getStageId());
        dto.setType(stage.getName()); // Map name to type
        dto.setState(stage.getStatus()); // Map status to state

        // Create stage stats
        CohortDTO.StageStatsDTO stageStats = new CohortDTO.StageStatsDTO();
        stageStats.setId(stage.getStageId());
        stageStats.setCohortID(stage.getCohort() != null ? stage.getCohort().getCohortId() : 0L);
        stageStats.setPeopleEntered(stage.getEntering() != null ? stage.getEntering() : 0);
        stageStats.setPeopleInactive(stage.getCurrent() != null ? stage.getCurrent() : 0);
        stageStats.setPeopleExit(stage.getExiting() != null ? stage.getExiting() : 0);
        stageStats.setOverall(stage.getCurrent() != null ? stage.getCurrent() : 0);
        
        dto.setStageStats(stageStats);

        return dto;
    }

    private CohortDTO.StatisticsDTO calculateStatistics(Cohort cohort) {
        CohortDTO.StatisticsDTO stats = new CohortDTO.StatisticsDTO();
        
        try {
            // Calculate basic statistics
            long totalUsers = cohort.getUsers() != null ? cohort.getUsers().size() : 0;
            long totalStages = cohort.getStages() != null ? cohort.getStages().size() : 0;
            
            // Calculate active users (you might need to adjust this based on your business logic)
            long activeUsers = cohort.getUsers() != null ? 
                cohort.getUsers().stream()
                    .filter(user -> "ACTIVE".equalsIgnoreCase(user.getStatus()))
                    .count() : 0;
            
            // Calculate total sessions
            long totalSessions = cohort.getUsers() != null ? 
                cohort.getUsers().stream()
                    .mapToLong(user -> {
                        try {
                            return user.getSessions() != null ? user.getSessions().size() : 0;
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .sum() : 0;
            
            stats.setTotalUsers(totalUsers);
            stats.setActiveUsers(activeUsers);
            stats.setInactiveUsers(totalUsers - activeUsers);
            stats.setTotalSessions(totalSessions);
            stats.setTotalStages(totalStages);
            stats.setActiveUserPercentage(totalUsers > 0 ? (double) activeUsers / totalUsers * 100 : 0.0);
            
            // Initialize empty lists for summaries (you can populate these based on your needs)
            stats.setSessionSummaries(Collections.emptyList());
            stats.setStageSummaries(Collections.emptyList());
            
        } catch (Exception e) {
            // Handle any exceptions during statistics calculation
            stats.setTotalUsers(0L);
            stats.setActiveUsers(0L);
            stats.setInactiveUsers(0L);
            stats.setTotalSessions(0L);
            stats.setTotalStages(0L);
            stats.setActiveUserPercentage(0.0);
            stats.setSessionSummaries(Collections.emptyList());
            stats.setStageSummaries(Collections.emptyList());
        }
        
        return stats;
    }
}