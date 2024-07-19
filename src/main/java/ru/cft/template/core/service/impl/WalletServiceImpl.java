package ru.cft.template.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.cft.template.api.dto.WalletDto;
import ru.cft.template.api.dto.WalletResponseDto;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.entity.Wallet;
import ru.cft.template.core.exception.DefaultException;
import ru.cft.template.core.jwt.JwtTokenUtils;
import ru.cft.template.core.repository.UserRepository;
import ru.cft.template.core.repository.WalletRepository;
import ru.cft.template.core.service.WalletService;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final Random random = new Random();

    @Override
    public Wallet createWallet() {
        log.info("Creating new wallet");
        Wallet userWallet = new Wallet();
        userWallet.setWalletBalance(100L);
        userWallet.setLastUpdate(LocalDateTime.now());

        boolean walletCreated = false;
        while (!walletCreated) {
            try {
                userWallet.setWalletNumber(generateUniqueWalletNumber());
                userWallet = repository.save(userWallet);
                walletCreated = true;
            } catch (DataIntegrityViolationException e) {
                log.warn("Wallet number collision detected, retrying generation");
                // Repeat generation
            }
        }

        log.info("Wallet created: {}", userWallet);
        return userWallet;
    }

    private Long generateUniqueWalletNumber() {
        return random.nextInt((10000000 - 1000000) + 1) + 1000000L;
    }

    @Override
    public WalletDto getUserWallet(Authentication authentication) {
        log.info("Fetching user wallet details");
        UUID id = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID: " + id + " not found"));

        WalletDto walletDto = new WalletDto (
                user.getWallet().getWalletId(),
                user.getWallet().getWalletNumber(),
                user.getWallet().getWalletBalance(),
                user.getWallet().getLastUpdate()
        );

        log.info("User wallet details retrieved: {}", walletDto);
        return walletDto;
    }

    @Override
    public WalletResponseDto hesoyam(Authentication authentication) {
        log.info("Fetching user wallet details");
        UUID id = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID: " + id + " not found"));
        Wallet userWallet = user.getWallet();

        Random random = new Random();
        int chance = random.nextInt(100);

        if (chance < 25) {
            log.info("User won 10 currency units!");
            userWallet.setWalletBalance(userWallet.getWalletBalance() + 10);
            repository.save(userWallet);
        } else {
            log.info("User did not win.");
        }

        return new WalletResponseDto(
                userWallet.getWalletId(),
                userWallet.getWalletBalance()
        );
    }

    public Wallet findWalletByNumber(Long walletNumber) {
        log.info("Fetching wallet details by wallet number: {}", walletNumber);
        return repository.findByWalletNumber(walletNumber)
                .orElseThrow(() -> new DefaultException("Wallet not found for the given number"));
    }

}

