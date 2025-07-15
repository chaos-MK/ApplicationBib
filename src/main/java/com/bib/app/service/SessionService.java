package com.bib.app.service;

import com.bib.app.entities.Session;
import com.bib.app.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SessionService implements ISessionService {
    private final SessionRepository sessionRepository;
    
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
    
    @Override
    public Session add(Session session) {
        return this.sessionRepository.save(session);
    }
    
    @Override
    public List<Session> getSessionsByUserId(Long userId) {
        return this.sessionRepository.findByUserUserId(userId);
    }
}