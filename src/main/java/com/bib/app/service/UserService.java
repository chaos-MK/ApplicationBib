package com.bib.app.service;

import com.bib.app.entities.Cohort;
import com.bib.app.entities.Company;
import com.bib.app.entities.Project;
import com.bib.app.entities.User;
import com.bib.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;

    @Override
    public User add(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User Deleteone(Long userID) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("user not found with ID: " + userID));

        userRepository.deleteById(userID);
        return user;
    }

    @Override
    public List<User> getsessionsByUserId(Long userID) {
        return null;
    }

    @Override
    public List<User> searchByUser(Long UserID) {
        return null;
    }


    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Override
    public User getOneUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found with ID: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }
}


