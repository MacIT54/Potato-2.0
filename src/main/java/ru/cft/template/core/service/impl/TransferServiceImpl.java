package ru.cft.template.core.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.cft.template.api.dto.*;
import ru.cft.template.core.entity.Transfer;
import ru.cft.template.core.entity.User;
import ru.cft.template.core.entity.Wallet;
import ru.cft.template.core.exception.BadTransactionException;
import ru.cft.template.core.exception.InvalidDataException;
import ru.cft.template.core.jwt.JwtTokenUtils;
import ru.cft.template.core.repository.TransferRepository;
import ru.cft.template.core.repository.UserRepository;
import ru.cft.template.core.repository.WalletRepository;
import ru.cft.template.core.mapper.TransferMapper;
import ru.cft.template.core.service.TransferService;

import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final UserServiceImpl userService;
    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final WalletServiceImpl walletService;

    @Override
    @Transactional
    public TransferDto createTransfer(Authentication authentication, RequestTransferDto request) {
        log.info("Creating transfer for user with authentication: {}", authentication);
        User user = userService.getUserAuth(authentication);
        Wallet senderWallet = user.getWallet();

        Transfer transfer = TransferMapper.createTransferEntity(request, senderWallet, user.getWallet());
        log.debug("Created transfer entity: {}", transfer);

        createPayment(transfer, request, senderWallet, request.getTransferType());

        senderWallet.setWalletBalance(senderWallet.getWalletBalance() - request.getAmount());
        Wallet receiverWalletId = walletRepository.findByWalletNumber(request.getReceiverWallet())
                .orElseThrow(() -> new UsernameNotFoundException("User with ID: not found"));
        walletRepository.save(senderWallet);
        transfer.setReceiverWallet(receiverWalletId);
        transfer.setStatus(TransferStatusDto.PAID);
        transferRepository.save(transfer);
        log.info("Transfer saved with status PAID: {}", transfer);

        return TransferMapper.createTransferDto(transfer, request);
    }

    @Override
    public TransferDto getTransfer(Authentication authentication, String id) {
        log.info("Fetching transfer with ID: {}", id);
        UUID userId = jwtTokenUtils.getUserIdFromAuthentication(authentication);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID: " + userId + " not found"));
        Transfer transfer = transferRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new InvalidDataException("Transfer not found"));
        if (user.getWallet() != transfer.getSenderWallet()) {
            log.error("Transfer not found for user with ID: {}", userId);
            throw new BadTransactionException("Transfer not found");
        }
        log.info("Transfer found: {}", transfer);
        return TransferMapper.mapTransferToDto(transfer);
    }

    @Override
    public List<TransferDto> getTransfersHistory(Authentication authentication, TransferTypeDto type, TransferStatusDto status, UUID receiverId) {
        log.info("Fetching transfer history for receiver ID: {}", receiverId);
        if (type != null && status != null) {
            return findByReceiverWallet_WalletIdAndTypeAndStatus(receiverId, type, status);
        } else if (type != null) {
            return findByReceiverWallet_WalletIdAndType(receiverId, type);
        } else {
            return findByReceiverWallet_WalletId(receiverId);
        }
    }

    private List<TransferDto> findByReceiverWallet_WalletId(UUID receiverId) {
        log.debug("Finding transfers by receiver wallet ID: {}", receiverId);
        List<Transfer> transfers = transferRepository.findByReceiverWallet_WalletId(receiverId);
        return TransferMapper.mapTransfersToTransferDto(transfers);
    }

    private List<TransferDto> findByReceiverWallet_WalletIdAndType(UUID receiverId, TransferTypeDto type) {
        log.debug("Finding transfers by receiver wallet ID: {} and type: {}", receiverId, type);
        List<Transfer> transfers = transferRepository.findByReceiverWallet_WalletIdAndType(receiverId, type);
        return TransferMapper.mapTransfersToTransferDto(transfers);
    }

    private List<TransferDto> findByReceiverWallet_WalletIdAndTypeAndStatus(UUID receiverId, TransferTypeDto type, TransferStatusDto status) {
        log.debug("Finding transfers by receiver wallet ID: {}, type: {}, and status: {}", receiverId, type, status);
        List<Transfer> transfers = transferRepository.findByReceiverWallet_WalletIdAndTypeAndStatus(receiverId, type, status);
        return TransferMapper.mapTransfersToTransferDto(transfers);
    }

    private void createPayment(Transfer transaction, RequestTransferDto request, Wallet senderWallet, TransferTypeDto type) {
        log.info("Creating payment for transaction: {}", transaction);
        transaction.setType(type);
        Wallet receiverWallet = determineReceiverWallet(request, type);
        request.setReceiverWallet(receiverWallet.getWalletNumber());

        validateTransfer(request, senderWallet, transaction);

        receiverWallet.setWalletBalance(receiverWallet.getWalletBalance() + request.getAmount());
        walletRepository.save(receiverWallet);
        log.info("Payment created and receiver wallet updated: {}", receiverWallet);
    }

    private Wallet determineReceiverWallet(RequestTransferDto request, TransferTypeDto type) {
        Wallet receiverWallet;
        if (type == TransferTypeDto.USER) {
            if (request.getReceiverPhone() != 0L) {
                receiverWallet = userService.findUserByPhone(request.getReceiverPhone()).getWallet();
            } else if (request.getReceiverWallet() != 0L) {
                receiverWallet = walletService.findWalletByNumber(request.getReceiverWallet());
            } else {
                throw new InvalidDataException("Receiver phone or wallet number must be provided for USER transfer");
            }
        } else if (type == TransferTypeDto.SERVICE) {
            receiverWallet = walletService.findWalletByNumber(request.getReceiverWallet());
        } else {
            throw new InvalidDataException("Invalid transfer type");
        }

        return receiverWallet;
    }

    private void validateTransfer(RequestTransferDto request, Wallet senderWallet, Transfer transaction) {
        if (!isValidTransaction(request)) {
            log.error("Invalid transaction request: {}", request);
            transaction.setStatus(TransferStatusDto.UNPAID);
            transferRepository.save(transaction);
            throw new BadTransactionException("Invalid transaction request");
        }

        if (!isValidWallet(request, senderWallet)) {
            log.error("Insufficient funds or wallet not found for request: {}", request);
            transaction.setStatus(TransferStatusDto.UNPAID);
            transferRepository.save(transaction);
            throw new BadTransactionException("Insufficient funds or wallet not found");
        }
    }

    private boolean isValidWallet(RequestTransferDto request, Wallet senderWallet) {
        return senderWallet != null && senderWallet.getWalletBalance() >= request.getAmount();
    }

    private boolean isValidTransaction(RequestTransferDto body) {
        return body.getAmount() > 0;
    }
}
