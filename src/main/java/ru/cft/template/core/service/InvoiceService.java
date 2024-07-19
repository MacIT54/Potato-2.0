package ru.cft.template.core.service;

import org.springframework.security.core.Authentication;
import ru.cft.template.api.dto.InvoiceDto;
import ru.cft.template.api.dto.RequestInvoiceDto;
import ru.cft.template.api.dto.TransferStatusDto;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceService {
    InvoiceDto createInvoice(Authentication authentication, RequestInvoiceDto body);
    InvoiceDto getInvoice(Authentication authentication, String id);
    List<InvoiceDto> getSentInvoices(Authentication authentication, TransferStatusDto status, LocalDateTime startDate, LocalDateTime endDate);
    List<InvoiceDto> getReceivedInvoices(Authentication authentication, TransferStatusDto status, LocalDateTime startDate, LocalDateTime endDate);
    TransferStatusDto payInvoice(Authentication authentication, String id);
    void deleteInvoice(Authentication authentication, String id);
    InvoiceDto getOldestUnpaidInvoice(Authentication authentication);
    long getTotalOutStanding(Authentication authentication);
}
