package com.clever;

import com.clever.dao.AccountDao;
import com.clever.dao.AccountDaoImpl;
import com.clever.entity.Account;
import com.clever.service.AccountService;
import com.clever.service.AccountServiceImpl;

import java.math.BigDecimal;

public class CleverRunner {

    public static void main(String[] args) {
        AccountService accountService = AccountServiceImpl.getInstance();
        Account account;
        account = accountService.withdraw(7694927649L, "Sber-Bank", BigDecimal.valueOf(1200));
        System.out.println(account);
        account = accountService.replenish(7694927649L, "Sber-Bank", BigDecimal.valueOf(1700));
        System.out.println(account);
        System.out.println("проверка");

        accountService.transferMoney(8676786786L,
                8299393939L,
                "Alfa-Bank",
                "Sber-Bank",
                BigDecimal.valueOf(1500));
    }
}
