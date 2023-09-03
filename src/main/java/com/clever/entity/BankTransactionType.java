package com.clever.entity;

public enum BankTransactionType {
    REPLENISH("Пополнение счета"),
    WITHDRAW("Снятие денег со счета"),
    TRANSFER("Перевод"),
    INTEREST("Начисление процентов");
    private final String value;

    BankTransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
