package ru.cft.template.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.cft.template.api.dto.TransferStatusDto;
import ru.cft.template.api.dto.TransferTypeDto;
import ru.cft.template.core.entity.Transfer;

import java.util.List;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
    List<Transfer> findByReceiverWallet_WalletId(UUID walletId);

    List<Transfer> findByReceiverWallet_WalletIdAndType(UUID receiverId, TransferTypeDto type);

    List<Transfer> findByReceiverWallet_WalletIdAndTypeAndStatus(UUID receiverId, TransferTypeDto type, TransferStatusDto status);
}
