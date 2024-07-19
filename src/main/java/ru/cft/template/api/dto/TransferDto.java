package ru.cft.template.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TransferDto {
    @NotNull
    private UUID transferId;

    @Positive
    private long amount;

    @NotNull
    private TransferTypeDto transferType;

    @Pattern(regexp = "7\\d{10}", message = "Receiver phone number must be 11 digits starting with '7'")
    private long receiverPhone;

    @NotNull
    private long receiverWallet;

    @Size(max = 50)
    private String serviceNumber;

    @NotNull
    private TransferStatusDto transferStatus;

    @NotNull
    @PastOrPresent
    private LocalDateTime transferDate;
}
