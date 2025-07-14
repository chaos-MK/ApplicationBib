package com.bib.app.service;

import com.bib.app.entities.Cohort;
import com.bib.app.entities.User;

import java.util.List;

public interface IUserService {

        User add(User user);

        User Deleteone(Long userID);

        List<User> getsessionsByUserId(Long userID);

        List<User> searchByUser(Long UserID);


        void deleteAllUsers();
        User getOneUser(Long id) ;

        List<User> getAllUsers() ;

    }
