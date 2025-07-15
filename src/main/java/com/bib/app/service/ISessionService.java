package com.bib.app.service;

import java.util.List;

import com.bib.app.entities.Session;
import com.bib.app.entities.User;

public interface ISessionService {
    Session add(Session session);
    List<Session> getSessionsByUserId(Long userId);
}
