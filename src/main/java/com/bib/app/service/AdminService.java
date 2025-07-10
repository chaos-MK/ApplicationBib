package com.bib.app.service;

import com.bib.app.entities.Admin;
import com.bib.app.repository.AdminRepository;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin createAdmin(Admin admin) {
        if (adminRepository.existsById(admin.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    public Admin getAdminByUsername(String username) {
        return adminRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public Admin updateAdmin(String username, Admin adminDetails) {
        Admin admin = getAdminByUsername(username);
        admin.setPassword(passwordEncoder.encode(adminDetails.getPassword()));
        admin.setRole(adminDetails.getRole());
        return adminRepository.save(admin);
    }
    
    public List<Admin> findAllAdmins() {
        return adminRepository.findAll();
    }

    public void deleteAdmin(String username) {
        adminRepository.deleteById(username);
    }
}