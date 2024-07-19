package ru.cft.template.core.service;

import org.springframework.security.core.Authentication;
import ru.cft.template.api.dto.*;

import java.util.List;
import java.util.UUID;

public interface TransferService {
    TransferDto createTransfer(Authentication authentication, RequestTransferDto request);

    TransferDto getTransfer(Authentication authentication, String id);

    List<TransferDto> getTransfersHistory(Authentication authentication, TransferTypeDto type, TransferStatusDto status, UUID receiverId);
}
