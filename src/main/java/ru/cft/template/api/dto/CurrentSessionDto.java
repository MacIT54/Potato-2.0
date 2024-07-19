package ru.cft.template.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CurrentSessionDto {
    @NotNull
    private UUID id;

    @NotNull
    private UUID userId;

    @NotNull
    @PastOrPresent
    private LocalDate expirationDate;

    private boolean active;
}
