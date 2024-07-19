package ru.cft.template.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.cft.template.api.dto.RequestUpdateUserDto;
import ru.cft.template.api.dto.UserDto;
import ru.cft.template.core.service.UserService;

import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "users")
@Tag(name = "api.user.tag.name", description = "api.user.tag.description")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UUID createUser(@RequestBody UserDto user) {
        return userService.createUser(user);
    }

    @PatchMapping
    public UserDto updateUser(Authentication authentication, @RequestBody RequestUpdateUserDto preEditUser) {
        return userService.updateUser(authentication, preEditUser);
    }

    @GetMapping
    public UserDto getUser(Authentication authentication) {
        return userService.getUser(authentication);
    }
}
