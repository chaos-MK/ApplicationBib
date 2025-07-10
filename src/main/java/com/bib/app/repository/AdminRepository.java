package com.bib.app.repository;

import com.bib.app.entities.Admin;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends CrudRepository<Admin, String> {
    Optional<Admin> findByUsername(String username);
    List<Admin> findAll();
}

