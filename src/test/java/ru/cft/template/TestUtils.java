package ru.cft.template;

import ru.cft.template.api.dto.RequestUpdateUserDto;
import ru.cft.template.api.dto.UserDto;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.entity.Wallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestUtils {
    public static Wallet createMockWallet() {
        Wallet mockWallet = new Wallet();
        mockWallet.setWalletId(UUID.randomUUID());
        mockWallet.setWalletBalance(100);
        mockWallet.setWalletNumber(1012233);
        mockWallet.setLastUpdate(LocalDateTime.now());
        return mockWallet;
    }

    public static User createMockUser() {
        User mockUser = new User();
        mockUser.setFirstName("John");
        mockUser.setLastName("Madison");
        mockUser.setEmail("jm1@example.com");
        mockUser.setPassword("Password123");
        mockUser.setLastUpdateDate(LocalDateTime.now());
        mockUser.setRegistrationDate(LocalDateTime.now());
        mockUser.setWallet(createMockWallet());
        return mockUser;
    }

    public static UserDto createMockUserDto() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Madison");
        userDto.setEmail("jm1@example.com");
        userDto.setBirthDate(LocalDate.of(1990, 1, 1));
        userDto.setPhoneNumber(78005553531L);
        userDto.setPassword("Password123");
        userDto.setLastUpdateDate(LocalDateTime.now());
        userDto.setRegistrationDate(LocalDateTime.now());
        return userDto;
    }

    public static RequestUpdateUserDto createMockRequestUpdateUserDto() {
        RequestUpdateUserDto userDto = new RequestUpdateUserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Madison");
        userDto.setBirthDate(LocalDate.of(1990, 1, 1));
        return userDto;
    }
}
