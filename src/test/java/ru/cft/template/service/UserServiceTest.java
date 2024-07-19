package ru.cft.template.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.cft.template.TestUtils;
import ru.cft.template.api.dto.RequestUpdateUserDto;
import ru.cft.template.api.dto.UserDto;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.entity.Wallet;
import ru.cft.template.core.repository.UserRepository;
import ru.cft.template.core.service.WalletService;
import ru.cft.template.core.service.impl.UserServiceImpl;
import ru.cft.template.core.jwt.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateUser() {
        Wallet mockWallet = TestUtils.createMockWallet();
        when(walletService.createWallet()).thenReturn(mockWallet);

        UserDto userDto = TestUtils.createMockUserDto();

        User mockUser = TestUtils.createMockUser();

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        }).when(userRepository).save(any(User.class));

        UUID userId = userService.createUser(userDto);

        assertNotNull(userId);
    }

    @Test
    public void testGetUser() {
        Wallet mockWallet = TestUtils.createMockWallet();
        when(walletService.createWallet()).thenReturn(mockWallet);

        UUID userId = UUID.randomUUID();
        User mockUser = TestUtils.createMockUser();
        mockUser.setId(userId);

        when(jwtTokenUtils.getUserIdFromAuthentication(any(Authentication.class))).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        UserDto resultUserDto = userService.getUser(mock(Authentication.class));

        assertEquals(mockUser.getId(), resultUserDto.getId());
        assertEquals(mockUser.getFirstName(), resultUserDto.getFirstName());
        assertEquals(mockUser.getLastName(), resultUserDto.getLastName());
        assertEquals(mockUser.getEmail(), resultUserDto.getEmail());
    }

    @Test
    public void testUpdateUser() {
        Wallet mockWallet = TestUtils.createMockWallet();
        UUID userId = UUID.randomUUID();
        User mockUser = TestUtils.createMockUser();
        mockUser.setId(userId);

        when(jwtTokenUtils.getUserIdFromAuthentication(any(Authentication.class))).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        RequestUpdateUserDto updateUserDto = TestUtils.createMockRequestUpdateUserDto();
        updateUserDto.setFirstName("Updatedjohn");
        updateUserDto.setLastName("Updatedmadison");

        User updatedUser = TestUtils.createMockUser();
        updatedUser.setId(userId);
        updatedUser.setFirstName(updateUserDto.getFirstName());
        updatedUser.setLastName(updateUserDto.getLastName());
        updatedUser.setLastUpdateDate(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto resultUserDto = userService.updateUser(mock(Authentication.class), updateUserDto);

        assertEquals(updatedUser.getId(), resultUserDto.getId());
        assertEquals(updatedUser.getFirstName(), resultUserDto.getFirstName());
        assertEquals(updatedUser.getLastName(), resultUserDto.getLastName());
        assertEquals(updatedUser.getEmail(), resultUserDto.getEmail());
    }
}


