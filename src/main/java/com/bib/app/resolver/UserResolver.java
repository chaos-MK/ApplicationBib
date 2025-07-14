package com.bib.app.resolver;

import com.bib.app.dto.UserDTO;
import com.bib.app.entities.Cohort;
import com.bib.app.entities.User;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserResolver {

    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }

        // Initialize cohort to avoid lazy loading issues
        Hibernate.initialize(user.getCohort());

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setStartDate(user.getStartDate() != null ? user.getStartDate().toString() : null);
        dto.setStatus(user.getStatus());
        dto.setHasGraphs(user.getHasGraphs());
        dto.setHasDashboard(user.getHasDashboard());

        // Set cohort information if available
        Cohort cohort = user.getCohort();
        if (cohort != null) {
            dto.setCohortId(cohort.getCohortId());
            dto.setCohortName(cohort.getCohortName());
        }

        return dto;
    }

    public List<UserDTO> convertToDTO(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }

        // Initialize cohort for all users to avoid lazy loading issues
        users.forEach(user -> Hibernate.initialize(user.getCohort()));

        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public User convertToEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUserName(dto.getUserName());
        user.setStartDate(dto.getStartDate() != null ? java.time.LocalDate.parse(dto.getStartDate()) : null);
        user.setStatus(dto.getStatus());
        user.setHasGraphs(dto.getHasGraphs());
        user.setHasDashboard(dto.getHasDashboard());
        // Note: Cohort is not set here; it should be set by the service using cohortId
        return user;
    }
}