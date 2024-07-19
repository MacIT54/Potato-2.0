package ru.cft.template.core.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.cft.template.api.dto.UserDto;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.entity.Wallet;

import java.time.LocalDateTime;

public class UserMapper {
    public static UserDto mapUserToResponse(User user) {
        return UserDto.builder()
                .id(user.getId())
                .walletId(user.getWallet().getWalletId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .password(user.getPassword())
                .registrationDate(user.getRegistrationDate())
                .lastUpdateDate(user.getLastUpdateDate())
                .birthDate(user.getBirthDate())
                .build();
    }

    public static User mapUserToEntity(UserDto dto, PasswordEncoder passwordEncoder, Wallet newWallet) {
        return User.builder()
                .birthDate(dto.getBirthDate())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .wallet(newWallet)
                .lastUpdateDate(LocalDateTime.now())
                .registrationDate(LocalDateTime.now())
                .build();
    }
}
