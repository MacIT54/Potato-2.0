package ru.cft.template.core.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.cft.template.api.dto.AuthorizationDto;
import ru.cft.template.api.dto.CurrentSessionDto;
import ru.cft.template.api.dto.SessionDto;
import ru.cft.template.core.entity.BannedToken;
import ru.cft.template.core.entity.Session;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.exception.UserNotFoundException;
import ru.cft.template.core.jwt.JwtTokenUtils;
import ru.cft.template.core.mapper.SessionMapper;
import ru.cft.template.core.repository.SessionRepository;
import ru.cft.template.core.repository.TokenRepository;
import ru.cft.template.core.service.SessionService;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final UserServiceImpl userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SessionDto createSession(AuthorizationDto body) {
        log.info("Creating session for user with phone: {}", body.getPhone());
        User user = userService.findUserByPhone(body.getPhone());
        if (user == null || !passwordEncoder.matches(body.getPassword(), user.getPassword())) {
            log.error("User not found or password incorrect for phone: {}", body.getPhone());
            throw new UserNotFoundException("User not found or password incorrect");
        }

        String token = jwtTokenUtils.generateToken(user);
        LocalDate expiredDate = jwtTokenUtils.getExpirationDateFromToken(token);

        Session sessions = new Session();
        sessions.setUserId(user.getId());
        sessions.setToken(token);
        sessions.setExpirationTime(expiredDate);
        sessionRepository.save(sessions);
        log.info("Session created and saved: {}", sessions);

        return SessionMapper.mapSessionToResponse(sessions);
    }

    @Override
    public CurrentSessionDto getCurrentSession(Authentication authentication) {
        log.info("Fetching current session for authentication: {}", authentication);
        String currentToken = (String) authentication.getCredentials();
        Session session = sessionRepository.findByToken(currentToken)
                .orElseThrow(() -> new UserNotFoundException("Session not found"));

        CurrentSessionDto currentSession = CurrentSessionDto.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .expirationDate(session.getExpirationTime())
                .active(jwtTokenUtils.getExpirationDateFromToken(currentToken).isAfter(LocalDate.now()))
                .build();
        log.info("Current session found: {}", currentSession);
        return currentSession;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteSession(Authentication authentication) {
        log.info("Deleting session for authentication: {}", authentication);
        UUID currentUserId = userService.getUserByAuth(authentication).getId();

        Session session = sessionRepository.findByToken(authentication.getCredentials().toString())
                .orElseThrow(() -> new UserNotFoundException("Session not found"));

        if (!session.getUserId().equals(currentUserId)) {
            log.error("User {} attempted to delete session not belonging to them", currentUserId);
            throw new UserNotFoundException("You can only delete your own sessions");
        }

        BannedToken bannedToken = new BannedToken();
        bannedToken.setToken(authentication.getCredentials().toString());
        tokenRepository.save(bannedToken);
        sessionRepository.deleteById(session.getId());

        log.info("Session {} deleted and token banned", session.getId());
        return ResponseEntity.ok().build();
    }


}

