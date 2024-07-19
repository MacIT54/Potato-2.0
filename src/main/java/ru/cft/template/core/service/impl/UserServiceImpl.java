package ru.cft.template.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.cft.template.api.dto.RequestUpdateUserDto;
import ru.cft.template.api.dto.UserDto;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.entity.Wallet;
import ru.cft.template.core.exception.DefaultException;
import ru.cft.template.core.exception.UserNotFoundException;
import ru.cft.template.core.mapper.UserMapper;
import ru.cft.template.core.repository.UserRepository;
import ru.cft.template.core.service.UserService;
import ru.cft.template.core.service.WalletService;
import ru.cft.template.core.jwt.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;

    public UUID createUser(UserDto body) {
        log.info("Creating user with username: {}", body.getFirstName() + body.getLastName());
        Wallet newWallet = walletService.createWallet();
        User user = UserMapper.mapUserToEntity(body, passwordEncoder, newWallet);

        userRepository.save(user);
        log.info("User created and saved: {}", user);

        return user.getId();
    }

    @Override
    public UserDto getUser(Authentication authentication) {
        log.info("Fetching user details for authentication: {}", authentication);
        UUID id = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " not found"));

        log.info("User details retrieved: {}", user);
        return UserMapper.mapUserToResponse(user);
    }

    @Override
    public UserDto updateUser(Authentication authentication, RequestUpdateUserDto body) {
        log.info("Updating user details for authentication: {}", authentication);
        UUID id = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " not found"));

        if (body.getFirstName() != null) {
            user.setFirstName(body.getFirstName());
        }
        if (body.getLastName() != null) {
            user.setLastName(body.getLastName());
        }
        if (body.getBirthDate() != null) {
            user.setBirthDate(body.getBirthDate());
        }

        user.setLastUpdateDate(LocalDateTime.now());

        userRepository.save(user);
        log.info("User details updated: {}", user);

        return UserMapper.mapUserToResponse(user);
    }

    public User getUserAuth(Authentication authentication) {
        log.info("Fetching authenticated user details: {}", authentication);
        UUID id = jwtTokenUtils.getUserIdFromAuthentication(authentication);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " not found"));

        log.info("Authenticated user details retrieved: {}", user);
        return user;
    }

    public User getUserByAuth(Authentication authentication) {
        log.info("Fetching user details by authentication: {}", authentication);
        UUID id = jwtTokenUtils.getUserIdFromAuthentication(authentication);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID: " + id + " not found"));

        log.info("User details retrieved by authentication: {}", user);
        return user;
    }

    public User findUserByPhone(Long phone) {
        log.info("Fetching user details by phone number: {}", phone);
        return userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new DefaultException("User not found for the given phone number"));
    }

    public User findUserById(UUID uuid) {
        log.info("Fetching user details by ID: {}", uuid);
        return userRepository.findById(uuid)
                .orElseThrow(() -> new DefaultException("User not found for the given ID"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user details by username: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " not found"));
    }
}

