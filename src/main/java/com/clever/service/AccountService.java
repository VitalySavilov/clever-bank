package com.clever.service;

import com.clever.entity.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account replenish(Long fromAccountNumber, String bankName, BigDecimal amount);

    Account withdraw(Long fromAccountNumber, String bankName, BigDecimal amount);

    void transferMoney(Long fromAccountNumber, Long toAccountNumber,
                       String fromBankName, String toBankName, BigDecimal amount);
}
