package ru.cft.template.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.cft.template.api.dto.TransferStatusDto;
import ru.cft.template.core.entity.Invoice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findBySenderId(UUID userId);
    List<Invoice> findByReceiverId(UUID userId);
    Optional<Invoice> findFirstByReceiverIdAndStatusOrderByTransactionDateAsc(UUID receiverId, TransferStatusDto status);

    List<Invoice> findBySenderIdAndStatus(UUID senderId, TransferStatusDto status);
    List<Invoice> findBySenderIdAndTransactionDateBetween(UUID senderId, LocalDateTime startDate, LocalDateTime endDate);
    List<Invoice> findBySenderIdAndStatusAndTransactionDateBetween(UUID senderId, TransferStatusDto status, LocalDateTime startDate, LocalDateTime endDate);

    List<Invoice> findByReceiverIdAndStatus(UUID receiverId, TransferStatusDto status);
    List<Invoice> findByReceiverIdAndTransactionDateBetween(UUID receiverId, LocalDateTime startDate, LocalDateTime endDate);
    List<Invoice> findByReceiverIdAndStatusAndTransactionDateBetween(UUID receiverId, TransferStatusDto status, LocalDateTime startDate, LocalDateTime endDate);
}