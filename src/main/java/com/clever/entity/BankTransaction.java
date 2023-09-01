package com.clever.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@ToString
public class BankTransaction {
    private Long id;
    private BigDecimal amount;
    private Timestamp transactionTimestamp;
    private String type;
    private Account account;
}
