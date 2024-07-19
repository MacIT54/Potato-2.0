package ru.cft.template.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.cft.template.api.dto.TransferStatusDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatusDto status;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column
    private String comment;

    @JoinColumn(name = "sender_id", referencedColumnName = "senderId")
    private UUID senderId;

    @JoinColumn(name = "receiver_id", referencedColumnName = "receiverId")
    private UUID receiverId;
}