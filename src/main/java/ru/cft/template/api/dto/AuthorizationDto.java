package ru.cft.template.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AuthorizationDto {
    @Pattern(regexp = "7\\d{10}", message = "Phone number must be 11 digits starting with '7'")
    private long phone;

    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!?])[A-Za-z\\d!?]*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (!?)")
    private String password;
}
