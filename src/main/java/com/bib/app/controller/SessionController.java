package com.bib.app.controller;

import com.bib.app.entities.Session;
import com.bib.app.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/session")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/addSession")
    public ResponseEntity<?> add(@RequestBody Session session) {
        try {
            Session savedProject = sessionService.add(session);

            if (savedProject == null) {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Session creation failed. Service returned null.\"}");
            }

            URI location = URI.create("/session/" + savedProject.getSessionId());

            return ResponseEntity
                .created(location)
                .body(savedProject);

        } catch (Exception ex) {
            ex.printStackTrace();

            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Unexpected error occurred: " + ex.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getSessionsByUserId(@PathVariable Long userId) {
        try {
            List<Session> sessions = sessionService.getSessionsByUserId(userId);
            
            if (sessions.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("{\"message\":\"No sessions found for user ID: " + userId + "\"}");
            }
            
            return ResponseEntity.ok(sessions);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Unexpected error occurred: " + ex.getMessage() + "\"}");
        }
    }
}