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

        // Convert users
        if (cohort.getUsers() != null) {
            dto.setUsers(cohort.getUsers().stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList()));
        } else {
            dto.setUsers(Collections.emptyList());
        }

        // Convert stages
        if (cohort.getStages() != null) {
            dto.setStages(cohort.getStages().stream()
                .map(this::toStageDTO)
                .collect(Collectors.toList()));
        } else {
            dto.setStages(Collections.emptyList());
        }

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
        stageStats.setCohortID(0L); // Set from stage entity if available
        stageStats.setPeopleEntered(stage.getEntering() != null ? stage.getEntering() : 0);
        stageStats.setPeopleInactive(stage.getCurrent() != null ? stage.getCurrent() : 0);
        stageStats.setPeopleExit(stage.getExiting() != null ? stage.getExiting() : 0);
        stageStats.setOverall(stage.getCurrent() != null ? stage.getCurrent() : 0);
        
        dto.setStageStats(stageStats);

        return dto;
    }
}