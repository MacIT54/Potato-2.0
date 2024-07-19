package ru.cft.template.core.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.cft.template.api.dto.RequestUpdateUserDto;
import ru.cft.template.api.dto.UserDto;

import java.util.UUID;

public interface UserService extends UserDetailsService{
    UUID createUser(UserDto user);

    UserDto getUser(Authentication authentication);

    UserDto updateUser(Authentication authentication, RequestUpdateUserDto preEditUser);
}
