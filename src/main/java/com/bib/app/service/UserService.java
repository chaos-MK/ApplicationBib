package com.bib.app.service;

import com.bib.app.entities.User;
import com.bib.app.entities.Cohort;
import com.bib.app.repository.UserRepository;
import com.bib.app.repository.CohortRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CohortRepository cohortRepository;
    
    // Create user
    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("Username already exists: " + user.getUserName());
        }
        
        // If cohort is specified, validate it exists
        if (user.getCohort() != null && user.getCohort().getCohortId() != null) {
            Optional<Cohort> cohort = cohortRepository.findById(user.getCohort().getCohortId());
            if (!cohort.isPresent()) {
                throw new RuntimeException("Cohort not found with ID: " + user.getCohort().getCohortId());
            }
            user.setCohort(cohort.get());
        }
        
        return userRepository.save(user);
    }
    
    // Get all users
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Get user by ID
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // Get user by username
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUserNameIgnoreCase(username);
    }
    
    // Update user
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        // Check if username is being changed and if new username already exists
        if (!user.getUserName().equals(userDetails.getUserName()) && 
            userRepository.existsByUserName(userDetails.getUserName())) {
            throw new RuntimeException("Username already exists: " + userDetails.getUserName());
        }
        
        // Update fields
        user.setUserName(userDetails.getUserName());
        user.setStartDate(userDetails.getStartDate());
        user.setStatus(userDetails.getStatus());
        user.setHasGraphs(userDetails.getHasGraphs());
        user.setHasDashboard(userDetails.getHasDashboard());
        
        // Update cohort if provided
        if (userDetails.getCohort() != null && userDetails.getCohort().getCohortId() != null) {
            Optional<Cohort> cohort = cohortRepository.findById(userDetails.getCohort().getCohortId());
            if (!cohort.isPresent()) {
                throw new RuntimeException("Cohort not found with ID: " + userDetails.getCohort().getCohortId());
            }
            user.setCohort(cohort.get());
        }
        
        return userRepository.save(user);
    }
    
    // Delete user
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        userRepository.delete(user);
    }
    
    // Get users by cohort ID
    @Transactional(readOnly = true)
    public List<User> getUsersByCohortId(Long cohortId) {
        return userRepository.findByCohortCohortId(cohortId);
    }
    
    // Get users by cohort name
    @Transactional(readOnly = true)
    public List<User> getUsersByCohortName(String cohortName) {
        return userRepository.findUsersByCohortName(cohortName);
    }
    
    // Get users by status
    @Transactional(readOnly = true)
    public List<User> getUsersByStatus(String status) {
        return userRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<User> getUsersWithGraphs() {
        return userRepository.findByHasGraphsTrue();
    }
    
    @Transactional(readOnly = true)
    public List<User> getUsersWithDashboard() {
        return userRepository.findByHasDashboardTrue();
    }
    
    // Assign user to cohort
    public User assignUserToCohort(Long userId, Long cohortId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        Cohort cohort = cohortRepository.findById(cohortId)
            .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + cohortId));
        
        user.setCohort(cohort);
        return userRepository.save(user);
    }
}