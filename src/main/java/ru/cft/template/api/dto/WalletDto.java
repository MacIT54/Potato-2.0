package ru.cft.template.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WalletDto {
    @NotNull
    private UUID walletId;

    @NotNull
    private long walletNumber;

    @PositiveOrZero
    private long walletBalance;

    @NotNull
    @PastOrPresent
    private LocalDateTime lastUpdate;
}
