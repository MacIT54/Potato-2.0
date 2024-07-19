package ru.cft.template.core.mapper;

import ru.cft.template.api.dto.InvoiceDto;
import ru.cft.template.api.dto.RequestInvoiceDto;
import ru.cft.template.api.dto.TransferStatusDto;
import ru.cft.template.core.entity.Invoice;
import ru.cft.template.core.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceMapper {

    public static InvoiceDto mapInvoiceToDto(Invoice invoice) {
        return InvoiceDto.builder()
                .invoiceId(invoice.getId())
                .cost(invoice.getAmount())
                .senderId(invoice.getSenderId())
                .receiverId(invoice.getReceiverId())
                .comment(invoice.getComment())
                .status(invoice.getStatus())
                .invoiceDate(invoice.getTransactionDate())
                .build();
    }

    public static List<InvoiceDto> mapInvoiceToDtoList(List<Invoice> invoices) {
        return invoices.stream()
                .map(InvoiceMapper::mapInvoiceToDto)
                .collect(Collectors.toList());
    }

    public static Invoice mapInvoiceToEntity(RequestInvoiceDto dto, User sender, User receiver) {
        Invoice invoice = new Invoice();
        invoice.setSenderId(sender.getId());
        invoice.setReceiverId(receiver.getId());
        invoice.setAmount(dto.getCost());
        invoice.setStatus(TransferStatusDto.UNPAID);
        invoice.setTransactionDate(LocalDateTime.now());
        invoice.setComment(dto.getComment());
        return invoice;
    }
}
