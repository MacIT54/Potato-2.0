package ru.cft.template.core.service;

import org.springframework.security.core.Authentication;
import ru.cft.template.api.dto.WalletDto;
import ru.cft.template.api.dto.WalletResponseDto;
import ru.cft.template.core.entity.Wallet;

public interface WalletService {
    Wallet createWallet ();
    WalletDto getUserWallet(Authentication authentication);

    WalletResponseDto hesoyam(Authentication authentication);

}
