package com.bib.app.controller;

import com.bib.app.entities.Project;
import com.bib.app.entities.User;
import com.bib.app.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/User")
public class UserController {
    private final IUserService userService ;
    @PostMapping("/addUser")
    public ResponseEntity<?> add(@RequestBody User user) {
        try {
            User savedProject = userService.add(user);

            if (savedProject == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\":\"User creation failed. Service returned null.\"}");
            }

            URI location = URI.create("/User/" + savedProject.getUserId());

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
}
