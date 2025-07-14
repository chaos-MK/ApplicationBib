package com.bib.app.controller;

import com.bib.app.entities.User;
import com.bib.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Create user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }
    
    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found with username: " + username, HttpStatus.NOT_FOUND);
        }
    }
    
    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    // Get users by cohort ID
    @GetMapping("/cohort/{cohortId}")
    public ResponseEntity<List<User>> getUsersByCohortId(@PathVariable Long cohortId) {
        List<User> users = userService.getUsersByCohortId(cohortId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    // Get users by cohort name
    @GetMapping("/cohort/name/{cohortName}")
    public ResponseEntity<List<User>> getUsersByCohortName(@PathVariable String cohortName) {
        List<User> users = userService.getUsersByCohortName(cohortName);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    // Get users by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable String status) {
        List<User> users = userService.getUsersByStatus(status);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    // Get users with graphs enabled
    @GetMapping("/with-graphs")
    public ResponseEntity<List<User>> getUsersWithGraphs() {
        List<User> users = userService.getUsersWithGraphs();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    // Get users with dashboard enabled
    @GetMapping("/with-dashboard")
    public ResponseEntity<List<User>> getUsersWithDashboard() {
        List<User> users = userService.getUsersWithDashboard();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    // Assign user to cohort
    @PutMapping("/{userId}/cohort/{cohortId}")
    public ResponseEntity<?> assignUserToCohort(@PathVariable Long userId, @PathVariable Long cohortId) {
        try {
            User updatedUser = userService.assignUserToCohort(userId, cohortId);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

