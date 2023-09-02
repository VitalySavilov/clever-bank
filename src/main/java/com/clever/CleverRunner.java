package com.clever;

import com.clever.dao.AccountDao;
import com.clever.dao.AccountDaoImpl;
import com.clever.entity.Account;
import com.clever.service.AccountService;
import com.clever.service.AccountServiceImpl;

import java.math.BigDecimal;

public class CleverRunner {

    public static void main(String[] args) {
        AccountDao accountDao = AccountDaoImpl.getInstance();
        AccountService accountService = AccountServiceImpl.getInstance();
        Account account = accountDao.findById(1L).orElseThrow();
        System.out.println(account);
        account = accountService.withdraw(account, BigDecimal.valueOf(1200));
        System.out.println(account);
        account = accountService.replenish(account, BigDecimal.valueOf(1700));
        System.out.println(account);
        System.out.println("проверка");

    }
}
