package com.clever.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

@Builder
@Getter
@Setter
@ToString
public class Account {
    private Long id;
    private Long accountNumber;
    private String cardNumber;
    private Date openDate;
    private BigDecimal balance;
    private String currency;
    private Bank bank;
    private AppUser appUser;
    private ArrayList<BankTransaction> bankTransactions;
}
