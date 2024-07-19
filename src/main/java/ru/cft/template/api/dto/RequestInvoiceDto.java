package ru.cft.template.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RequestInvoiceDto {
    @Positive
    private long cost;

    @NotNull
    private UUID senderId;

    @NotNull
    private UUID receiverId;

    @Size(max = 255)
    private String comment;

    @NotNull
    @PastOrPresent
    private LocalDateTime invoiceDate;
}
