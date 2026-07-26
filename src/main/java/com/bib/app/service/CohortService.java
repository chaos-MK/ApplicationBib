package com.bib.app.service;

import com.bib.app.entities.Cohort;
import com.bib.app.entities.Project;
import com.bib.app.entities.User;
import com.bib.app.repository.CohortRepository;
import com.bib.app.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
public class CohortService implements ICohortService {
    
    private final CohortRepository cohortRepository;
    private final ProjectRepository projectRepository;
    
    public CohortService(CohortRepository cohortRepository, ProjectRepository projectRepository) {
    	this.cohortRepository = cohortRepository;
    	this.projectRepository = projectRepository;
    }
    
    @Override
    public Cohort add(Cohort cohort) {
        return this.cohortRepository.save(cohort);
    }
    
    @Override
    public Cohort Deleteone(Long cohortId) {
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + cohortId));
        cohortRepository.deleteById(cohortId);
        return cohort;
    }
    
    @Override
    public List<User> getUsersByCohortId(Long cohortId) {
        Cohort cohort = cohortRepository.findById(cohortId)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + cohortId));
        return cohort.getUsers();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Cohort> searchByProject(Long projectId) {
        // Check if project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        
        // Option 1: Use separate queries to avoid MultipleBagFetchException
        List<Cohort> cohorts = cohortRepository.findByProjectIdWithUsers(projectId);
        
        // Force initialization of collections within the transaction
        for (Cohort cohort : cohorts) {
            // Initialize users collection if not already loaded
            if (cohort.getUsers() != null) {
                cohort.getUsers().size(); // Force initialization
                
                // Initialize sessions for each user
                for (User user : cohort.getUsers()) {
                    if (user.getSessions() != null) {
                        user.getSessions().size(); // Force initialization
                    }
                }
            }
            
            // Initialize stages collection
            if (cohort.getStages() != null) {
                cohort.getStages().size(); // Force initialization
            }
        }
        
        return cohorts;
    }
    
    // Alternative approach using multiple queries
    @Transactional(readOnly = true)
    public List<Cohort> searchByProjectAlternative(Long projectId) {
        // Check if project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
        
        // Get cohorts with users first
        List<Cohort> cohortsWithUsers = cohortRepository.findByProjectIdWithUsers(projectId);
        
        // Get cohorts with stages
        List<Cohort> cohortsWithStages = cohortRepository.findByProjectIdWithStages(projectId);
        
        // Merge the results (this is a simplified approach)
        // In a real application, you might want to use a Map to efficiently merge
        for (Cohort cohortWithUsers : cohortsWithUsers) {
            for (Cohort cohortWithStages : cohortsWithStages) {
                if (cohortWithUsers.getCohortId().equals(cohortWithStages.getCohortId())) {
                    cohortWithUsers.setStages(cohortWithStages.getStages());
                    break;
                }
            }
            
            // Initialize sessions for each user
            for (User user : cohortWithUsers.getUsers()) {
                if (user.getSessions() != null) {
                    user.getSessions().size(); // Force initialization
                }
            }
        }
        
        return cohortsWithUsers;
    }
    
    @Override
    public List<Cohort> getAllCohort() {
        return (List<Cohort>) cohortRepository.findAll();
    }
    
    @Override
    public void deleteAllCohorts() {
        cohortRepository.deleteAll();
    }
    
    @Override
    public Cohort getOneCohort(Long id) {
        return cohortRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + id));
    }
}