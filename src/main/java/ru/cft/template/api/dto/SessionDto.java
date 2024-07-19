package ru.cft.template.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SessionDto {
    @NotNull
    private UUID id;

    @NotNull
    private UUID userId;

    @NotBlank
    private String token;

    @NotNull
    @Future
    private LocalDate expirationTime;
}

