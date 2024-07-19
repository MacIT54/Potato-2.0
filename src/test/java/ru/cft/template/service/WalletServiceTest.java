package ru.cft.template.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import ru.cft.template.api.dto.WalletResponseDto;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.entity.Wallet;
import ru.cft.template.core.repository.UserRepository;
import ru.cft.template.core.repository.WalletRepository;
import ru.cft.template.core.jwt.JwtTokenUtils;
import ru.cft.template.core.service.impl.WalletServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    public void testCreateWallet() {
        Wallet mockWallet = new Wallet();
        mockWallet.setWalletNumber(12345678L);
        mockWallet.setWalletBalance(100L);
        mockWallet.setLastUpdate(LocalDateTime.now());

        when(walletRepository.save(any(Wallet.class))).thenReturn(mockWallet);

        Wallet createdWallet = walletService.createWallet();

        assertNotNull(createdWallet);
        assertEquals(12345678L, createdWallet.getWalletNumber());
        assertEquals(100L, createdWallet.getWalletBalance());
    }

    @Test
    public void testHesoyam() {
        UUID userId = UUID.randomUUID();
        User mockUser = new User();
        mockUser.setId(userId);

        Wallet mockWallet = new Wallet();
        mockWallet.setWalletId(UUID.randomUUID());
        mockWallet.setWalletBalance(100L);
        mockUser.setWallet(mockWallet);

        when(jwtTokenUtils.getUserIdFromAuthentication(any(Authentication.class))).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(walletRepository.save(any(Wallet.class))).thenReturn(mockWallet);

        WalletResponseDto result = walletService.hesoyam(mock(Authentication.class));

        assertNotNull(result);
    }

    @Test
    public void testFindWalletByNumber() {
        Long walletNumber = 12345678L;
        Wallet mockWallet = new Wallet();
        mockWallet.setWalletNumber(walletNumber);
        mockWallet.setWalletBalance(100L);
        mockWallet.setLastUpdate(LocalDateTime.now());

        when(walletRepository.findByWalletNumber(walletNumber)).thenReturn(Optional.of(mockWallet));

        Wallet foundWallet = walletService.findWalletByNumber(walletNumber);

        assertNotNull(foundWallet);
        assertEquals(walletNumber, foundWallet.getWalletNumber());
        assertEquals(100L, foundWallet.getWalletBalance());
    }
}

