package ru.cft.template.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.cft.template.api.dto.TransferStatusDto;
import ru.cft.template.api.dto.TransferTypeDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Data
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferTypeDto type;

    @Column
    private Long receiverPhone;

    @Column
    private Long maintenanceNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatusDto status;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id", referencedColumnName = "walletId")
    private Wallet senderWallet;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id", referencedColumnName = "walletId")
    private Wallet receiverWallet;
}