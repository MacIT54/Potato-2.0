package ru.cft.template.api.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;
import javax.validation.constraints.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private UUID id;

    @NotNull
    private String walletId;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotNull
    @Digits(integer = 20, fraction = 0)
    private long phoneNumber;

    @NotBlank
    @Size(max = 512)
    private String password;

    private LocalDateTime registrationDate;
    private LocalDateTime lastUpdateDate;

    @NotNull
    @Past
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be at most 100")
    private LocalDate birthDate;
}
