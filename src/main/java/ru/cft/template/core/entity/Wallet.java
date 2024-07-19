package ru.cft.template.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID walletId;

    @Column(nullable = false)
    private long walletNumber;

    @Column(nullable = false)
    private long walletBalance;

    @Column(nullable = false)
    private LocalDateTime lastUpdate =  LocalDateTime.now();
}
