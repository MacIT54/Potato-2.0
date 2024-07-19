package ru.cft.template.core.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.cft.template.api.dto.AuthorizationDto;
import ru.cft.template.api.dto.CurrentSessionDto;
import ru.cft.template.api.dto.SessionDto;

public interface SessionService {
    SessionDto createSession(AuthorizationDto session);

    CurrentSessionDto getCurrentSession(Authentication authentication);

    ResponseEntity<Void> deleteSession(Authentication authentication);
}
