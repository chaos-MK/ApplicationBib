package com.bib.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bib.app.entities.Admin;
import com.bib.app.service.AdminService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        try {
            
            Admin savedAdmin = adminService.createAdmin(admin);
            return new ResponseEntity<>(savedAdmin, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    

    @GetMapping
    public ResponseEntity<Admin> getAdminByUsername(@PathVariable String username) {
        try {
            Admin admin = adminService.getAdminByUsername(username);
            return new ResponseEntity<>(admin, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}