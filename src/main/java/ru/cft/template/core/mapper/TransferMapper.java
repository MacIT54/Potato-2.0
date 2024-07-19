package ru.cft.template.core.mapper;

import ru.cft.template.api.dto.RequestTransferDto;
import ru.cft.template.api.dto.TransferDto;
import ru.cft.template.api.dto.TransferStatusDto;
import ru.cft.template.api.dto.TransferTypeDto;
import ru.cft.template.core.entity.Invoice;
import ru.cft.template.core.entity.Transfer;
import ru.cft.template.core.entity.Wallet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TransferMapper {
    public static Transfer createTransferEntity(RequestTransferDto request, Wallet senderWallet, Wallet receiverWallet) {
        Transfer transfer = new Transfer();
        transfer.setAmount(request.getAmount());
        transfer.setTransactionDate(LocalDateTime.now());
        transfer.setStatus(TransferStatusDto.UNPAID);
        transfer.setSenderWallet(senderWallet);
        transfer.setReceiverPhone(request.getReceiverPhone());
        transfer.setReceiverWallet(receiverWallet);
        return transfer;
    }

    public static TransferDto createTransferDto(Transfer transfer, RequestTransferDto request) {
        return TransferDto.builder()
                .transferId(transfer.getId())
                .amount(transfer.getAmount())
                .transferType(transfer.getType())
                .receiverPhone(request.getReceiverPhone())
                .receiverWallet(request.getReceiverWallet())
                .serviceNumber(request.getServiceNumber())
                .transferStatus(transfer.getStatus())
                .transferDate(transfer.getTransactionDate())
                .build();
    }

    public static TransferDto mapTransferToDto(Transfer transfer) {
        return TransferDto.builder()
                .transferId(transfer.getId())
                .amount(transfer.getAmount())
                .transferType(transfer.getType())
                .receiverPhone(transfer.getReceiverPhone())
                .receiverWallet(transfer.getReceiverWallet().getWalletNumber())
                .serviceNumber(transfer.getMaintenanceNumber().toString())
                .transferStatus(transfer.getStatus())
                .transferDate(transfer.getTransactionDate())
                .build();
    }

    public static List<TransferDto> mapTransferToDtoList(List<Transfer> transfers) {
        return transfers.stream()
                .map(TransferMapper::mapTransferToDto)
                .collect(Collectors.toList());
    }

    public static List<TransferDto> mapTransfersToTransferDto(List<Transfer> transfers) {
        return transfers.stream()
                .map(transfer -> {
                    TransferDto dto = new TransferDto();
                    dto.setTransferId(transfer.getId());
                    dto.setAmount(transfer.getAmount());
                    dto.setTransferType(transfer.getType());
                    dto.setReceiverPhone(transfer.getReceiverPhone());
                    dto.setReceiverWallet(transfer.getReceiverWallet().getWalletNumber());
                    dto.setTransferStatus(transfer.getStatus());
                    dto.setTransferDate(transfer.getTransactionDate());
                    dto.setServiceNumber(transfer.getMaintenanceNumber() != null ? transfer.getMaintenanceNumber().toString() : "0");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static Transfer mapInvoiceToTransfer(Invoice invoice, Wallet senderWallet, Wallet receiverWallet) {
        Transfer transfer = new Transfer();
        transfer.setAmount(invoice.getAmount());
        transfer.setTransactionDate(LocalDateTime.now());
        transfer.setType(TransferTypeDto.SERVICE);
        transfer.setStatus(TransferStatusDto.PAID);
        transfer.setSenderWallet(senderWallet);
        transfer.setReceiverWallet(receiverWallet);
        return transfer;
    }
}
