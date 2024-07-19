package ru.cft.template.api.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenDto {
    private String token;
}

