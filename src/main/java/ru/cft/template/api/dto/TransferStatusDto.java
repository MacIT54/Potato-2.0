package ru.cft.template.api.dto;

import lombok.*;

@Getter
public enum TransferStatusDto {
    PAID, UNPAID, CANCELED;

    @Override
    public String toString() {
        return name();
    }
}
