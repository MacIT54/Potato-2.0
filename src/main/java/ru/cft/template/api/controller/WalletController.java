package ru.cft.template.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.template.api.dto.WalletDto;
import ru.cft.template.api.dto.WalletResponseDto;
import ru.cft.template.core.service.WalletService;

@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("wallet")
    public WalletDto getUser(Authentication authentication) {
        return walletService.getUserWallet(authentication);
    }

    @PostMapping("hesoyam")
    public WalletResponseDto hesoyam(Authentication authentication) {
        return walletService.hesoyam(authentication);
    }
}
