package com.clever.service;

import com.clever.dao.AccountDao;
import com.clever.dao.AccountDaoImpl;
import com.clever.entity.Account;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService{
    private final AccountDao accountDao = AccountDaoImpl.getInstance();
    private static final AccountServiceImpl INSTANCE = new AccountServiceImpl();

    private AccountServiceImpl() {
    }

    @Override
    public Account replenish(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountDao.saveOrUpdate(account);
        return account;
    }

    @Override
    public Account withdraw(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().subtract(amount));
        accountDao.saveOrUpdate(account);
        return account;
    }

    public static AccountServiceImpl getInstance() {
        return INSTANCE;
    }

}
