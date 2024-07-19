package ru.cft.template.core.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.cft.template.api.dto.*;
import ru.cft.template.core.entity.Invoice;
import ru.cft.template.core.entity.Transfer;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.entity.Wallet;
import ru.cft.template.core.exception.BadTransactionException;
import ru.cft.template.core.exception.UserNotFoundException;
import ru.cft.template.core.jwt.JwtTokenUtils;
import ru.cft.template.core.repository.InvoiceRepository;
import ru.cft.template.core.repository.TransferRepository;
import ru.cft.template.core.repository.WalletRepository;
import ru.cft.template.core.service.InvoiceService;
import ru.cft.template.core.mapper.InvoiceMapper;
import ru.cft.template.core.mapper.TransferMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserServiceImpl userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;

    @Override
    public InvoiceDto createInvoice(Authentication authentication, RequestInvoiceDto body) {
        log.info("Creating invoice for user with authentication: {}", authentication);
        User sender = userService.getUserByAuth(authentication);
        Wallet senderWallet = sender.getWallet();
        if (senderWallet == null) {
            log.error("Sender wallet not found for user: {}", sender.getId());
            throw new UserNotFoundException("Sender wallet not found");
        }
        User receiver = userService.findUserById(body.getReceiverId());
        Wallet receiverWallet = receiver.getWallet();
        if (receiverWallet == null) {
            log.error("Receiver wallet not found for user: {}", receiver.getId());
            throw new UserNotFoundException("Receiver wallet not found");
        }

        if (senderWallet == receiverWallet) {
            log.error("User {} attempted to bill themselves", sender.getId());
            throw new BadTransactionException("You can't bill yourself");
        }

        Invoice invoice = InvoiceMapper.mapInvoiceToEntity(body, sender, receiver);
        invoiceRepository.save(invoice);
        log.info("Invoice created and saved: {}", invoice);

        return InvoiceMapper.mapInvoiceToDto(invoice);
    }

    @Override
    public InvoiceDto getInvoice(Authentication authentication, String id) {
        log.info("Fetching invoice with ID: {}", id);
        Invoice invoice = invoiceRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + id + " not found"));
        log.info("Invoice found: {}", invoice);
        return InvoiceMapper.mapInvoiceToDto(invoice);
    }

    @Override
    public List<InvoiceDto> getSentInvoices(Authentication authentication, TransferStatusDto status, LocalDateTime startDate, LocalDateTime endDate) {
        UUID userId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        log.info("Fetching sent invoices for user ID: {} with filters - status: {}, startDate: {}, endDate: {}", userId, status, startDate, endDate);
        List<Invoice> invoices;

        if (status != null && startDate != null && endDate != null) {
            invoices = invoiceRepository.findBySenderIdAndStatusAndTransactionDateBetween(userId, status, startDate, endDate);
        } else if (status != null) {
            invoices = invoiceRepository.findBySenderIdAndStatus(userId, status);
        } else if (startDate != null && endDate != null) {
            invoices = invoiceRepository.findBySenderIdAndTransactionDateBetween(userId, startDate, endDate);
        } else {
            invoices = invoiceRepository.findBySenderId(userId);
        }

        log.debug("Found {} sent invoices for user ID: {}", invoices.size(), userId);
        return InvoiceMapper.mapInvoiceToDtoList(invoices);
    }

    @Override
    public List<InvoiceDto> getReceivedInvoices(Authentication authentication, TransferStatusDto status, LocalDateTime startDate, LocalDateTime endDate) {
        UUID userId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        log.info("Fetching received invoices for user ID: {} with filters - status: {}, startDate: {}, endDate: {}", userId, status, startDate, endDate);
        List<Invoice> invoices;

        if (status != null && startDate != null && endDate != null) {
            invoices = invoiceRepository.findBySenderIdAndStatusAndTransactionDateBetween(userId, status, startDate, endDate);
        } else if (status != null) {
            invoices = invoiceRepository.findByReceiverIdAndStatus(userId, status);
        } else if (startDate != null && endDate != null) {
            invoices = invoiceRepository.findBySenderIdAndTransactionDateBetween(userId, startDate, endDate);
        } else {
            invoices = invoiceRepository.findByReceiverId(userId);
        }

        log.debug("Found {} received invoices for user ID: {}", invoices.size(), userId);
        return InvoiceMapper.mapInvoiceToDtoList(invoices);
    }

    @Override
    @Transactional
    public TransferStatusDto payInvoice(Authentication authentication, String id) {
        log.info("Paying invoice with ID: {}", id);
        User payer = userService.getUserByAuth(authentication);
        Wallet payerWallet = payer.getWallet();
        if (payerWallet == null) {
            log.error("Payer wallet not found for user: {}", payer.getId());
            throw new UserNotFoundException("Payer wallet not found");
        }

        Invoice invoice = invoiceRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException("Invoice not found"));
        User sender = userService.findUserById(invoice.getSenderId());
        Wallet senderWallet = sender.getWallet();
        if (!invoice.getReceiverId().equals(payer.getId())) {
            log.error("User {} attempted to pay invoice not billed to them", payer.getId());
            throw new BadTransactionException("You can only pay invoices billed to you");
        }

        if ((payerWallet.getWalletBalance() - invoice.getAmount()) < 0) {
            log.error("Insufficient funds for user {} to pay invoice {}", payer.getId(), id);
            throw new BadTransactionException("Insufficient funds to pay the invoice");
        }

        payerWallet.setWalletBalance(payerWallet.getWalletBalance() - invoice.getAmount());
        senderWallet.setWalletBalance(senderWallet.getWalletBalance() + invoice.getAmount());
        walletRepository.save(payerWallet);
        walletRepository.save(senderWallet);

        invoice.setStatus(TransferStatusDto.PAID);
        invoiceRepository.save(invoice);
        log.info("Invoice {} paid and updated", id);

        Transfer transfer = TransferMapper.mapInvoiceToTransfer(invoice, senderWallet, payerWallet);
        transferRepository.save(transfer);
        log.info("Transfer created and saved: {}", transfer);

        return TransferStatusDto.PAID;
    }

    @Override
    public void deleteInvoice(Authentication authentication, String id) {
        log.info("Deleting invoice with ID: {}", id);
        User user = userService.getUserByAuth(authentication);
        UUID invoiceId = UUID.fromString(id);

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new UserNotFoundException("Invoice not found"));

        if (!invoice.getSenderId().equals(user.getId())) {
            log.error("User {} attempted to delete invoice not created by them", user.getId());
            throw new BadTransactionException("You can only delete your own invoices");
        }

        if (invoice.getStatus().equals(TransferStatusDto.PAID)) {
            log.error("Attempt to delete paid invoice {}", id);
            throw new BadTransactionException("Cannot delete paid invoice");
        }
        invoice.setStatus(TransferStatusDto.CANCELED);
        invoiceRepository.save(invoice);
        log.info("Invoice {} canceled and updated", id);
    }

    @Override
    public InvoiceDto getOldestUnpaidInvoice(Authentication authentication) {
        log.info("Fetching oldest unpaid invoice");
        UUID userId = jwtTokenUtils.getUserIdFromAuthentication(authentication);

        Optional<Invoice> oldestUnpaidInvoiceOpt = invoiceRepository.findFirstByReceiverIdAndStatusOrderByTransactionDateAsc(userId, TransferStatusDto.UNPAID);

        if (oldestUnpaidInvoiceOpt.isPresent()) {
            Invoice oldestUnpaidInvoice = oldestUnpaidInvoiceOpt.get();
            log.info("Found oldest unpaid invoice: {}", oldestUnpaidInvoice);
            return InvoiceMapper.mapInvoiceToDto(oldestUnpaidInvoice);
        } else {
            log.info("No unpaid invoices found for user ID: {}", userId);
            return null;
        }
    }

    @Override
    public long getTotalOutStanding(Authentication authentication) {
        log.info("Fetching total outstanding amount");
        UUID userId = jwtTokenUtils.getUserIdFromAuthentication(authentication);

        List<Invoice> unpaidInvoices = invoiceRepository.findByReceiverIdAndStatus(userId, TransferStatusDto.UNPAID);

        long totalOutstanding = unpaidInvoices.stream()
                .mapToLong(Invoice::getAmount)
                .sum();

        log.info("Total outstanding amount for user ID {}: {}", userId, totalOutstanding);
        return totalOutstanding;
    }
}
