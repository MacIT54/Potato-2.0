package ru.cft.template.core.mapper;

import ru.cft.template.api.dto.SessionDto;
import ru.cft.template.core.entity.Session;

public class SessionMapper {
    public static SessionDto mapSessionToResponse(Session session) {
        return SessionDto.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .token(session.getToken())
                .expirationTime(session.getExpirationTime())
                .build();
    }
}