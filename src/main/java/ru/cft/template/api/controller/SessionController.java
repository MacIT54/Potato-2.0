package ru.cft.template.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.cft.template.api.dto.AuthorizationDto;
import ru.cft.template.api.dto.CurrentSessionDto;
import ru.cft.template.api.dto.SessionDto;
import ru.cft.template.core.service.SessionService;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("sessions")
    public SessionDto createSession(@RequestBody AuthorizationDto session) {
        return sessionService.createSession(session);
    }

    @GetMapping("/sessions/current")
    public CurrentSessionDto getCurrentSession(Authentication authentication){
        return sessionService.getCurrentSession(authentication);
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Void> deleteSession(Authentication authentication){
        sessionService.deleteSession(authentication);
        return ResponseEntity.ok().build();
    }
}
