package ru.cft.template.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.cft.template.api.dto.*;
import ru.cft.template.core.service.TransferService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public TransferDto createTransfer (Authentication authentication, @RequestBody RequestTransferDto body) {
        return transferService.createTransfer(authentication, body);
    }

    @GetMapping("{id}")
    public TransferDto getTransfer(Authentication authentication, @PathVariable String id) {
        return transferService.getTransfer(authentication, id);
    }


    @GetMapping("/history")
    public List<TransferDto> getTransfersHistory(
            Authentication authentication,
            @RequestParam(required = false) TransferTypeDto type,
            @RequestParam(required = false) TransferStatusDto status,
            @RequestParam UUID receiverId) {

        return transferService.getTransfersHistory(authentication, type, status, receiverId);
    }

}
