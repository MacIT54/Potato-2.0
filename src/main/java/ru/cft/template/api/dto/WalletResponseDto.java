package ru.cft.template.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WalletResponseDto {
    @NotNull
    private UUID walletId;

    @PositiveOrZero
    private long walletBalance;
}



