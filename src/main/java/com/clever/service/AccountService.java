package com.clever.service;

import com.clever.entity.Account;

import java.math.BigDecimal;

public interface AccountService {

    Account replenish(Account account, BigDecimal amount);

    Account withdraw(Account account, BigDecimal amount);
}
