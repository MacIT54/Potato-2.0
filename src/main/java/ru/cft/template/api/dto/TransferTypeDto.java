package ru.cft.template.api.dto;

public enum TransferTypeDto {
    USER, SERVICE;

    @Override
    public String toString() {
        return name();
    }
}