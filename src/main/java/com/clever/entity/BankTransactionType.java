package com.clever.entity;

public enum BankTransactionType {
    REPLENISH("Пополнение счета"),
    WITHDRAW("Снятие денег со счета"),
    INTEREST("Начисление процентов");
    private final String value;

    BankTransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
