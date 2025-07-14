package com.bib.app.service;

import com.bib.app.entities.Session;
import com.bib.app.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionService implements ISessionService{
    private final SessionRepository sessionRepository;
    @Override
    public Session add(Session session) {
        return this.sessionRepository.save(session);
    }
}
