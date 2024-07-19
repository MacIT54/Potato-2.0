package ru.cft.template.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.cft.template.api.dto.InvoiceDto;
import ru.cft.template.api.dto.RequestInvoiceDto;
import ru.cft.template.api.dto.TransferStatusDto;
import ru.cft.template.core.service.InvoiceService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public InvoiceDto createInvoice(Authentication authentication, @RequestBody RequestInvoiceDto body) {
        return invoiceService.createInvoice(authentication, body);
    }

    @GetMapping("{id}")
    public InvoiceDto getInvoice(Authentication authentication, @PathVariable String id){
        return invoiceService.getInvoice(authentication, id);
    }

    @PostMapping("{id}/pay")
    public TransferStatusDto payInvoice(Authentication authentication, @PathVariable String id) {
        return invoiceService.payInvoice(authentication, id);
    }

    @GetMapping("/sent")
    public List<InvoiceDto> getSentInvoices(Authentication authentication,
                                            @RequestParam(required = false) TransferStatusDto status,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return invoiceService.getSentInvoices(authentication, status, startDate, endDate);
    }

    @GetMapping("/received")
    public List<InvoiceDto> getReceivedInvoices(Authentication authentication,
                                                @RequestParam(required = false) TransferStatusDto status,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return invoiceService.getReceivedInvoices(authentication, status, startDate, endDate);
    }
    @DeleteMapping("{id}/cancel")
    public ResponseEntity<Void> deleteInvoice(Authentication authentication, @PathVariable String id){
        invoiceService.deleteInvoice(authentication, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("oldestUnpaid")
    public InvoiceDto getOldestUnpaidInvoice(Authentication authentication) {
        return invoiceService.getOldestUnpaidInvoice(authentication);
    }

    @GetMapping("totalOutStanding")
    public long getTotalOutStanding(Authentication authentication) {
        return invoiceService.getTotalOutStanding(authentication);
    }

}
