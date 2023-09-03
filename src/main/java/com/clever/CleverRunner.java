package com.clever;

import com.clever.service.AccountService;
import com.clever.service.AccountServiceImpl;
import com.clever.service.InterestService;
import com.clever.service.InterestServiceImpl;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CleverRunner {
    private static final AccountService accountService = AccountServiceImpl.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        InterestService interestService = InterestServiceImpl.getInstance();
        ScheduledExecutorService interestChecker = Executors.newSingleThreadScheduledExecutor();
        Runnable task = interestService::chargeInterest;
        interestChecker.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
        while (true) {
            System.out.println("""
                    Выберите операцию:
                    1. Пополнение счета
                    2. Снятие средств со счета
                    3. Перевод средств
                    4. Exit""");
            int num = scanner.nextInt();
            System.out.println("Your choose - " + num);
            scanner.nextLine();
            if (num == 1) accountService.replenish(
                    initAccountNumber("Введите номер счета:"),
                    initBankName("Введите название банка:"),
                    initAmount("Введите сумму:"));
            else if (num == 2) accountService.withdraw(
                    initAccountNumber("Введите номер счета:"),
                    initBankName("Введите название банка:"),
                    initAmount("Введите сумму:"));
            else if (num == 3) accountService.transferMoney(
                    initAccountNumber("Введите номер счета отправителя:"),
                    initAccountNumber("Введите номер счета получателя:"),
                    initBankName("Введите название банка отправителя:"),
                    initBankName("Введите название банка получателя:"),
                    initAmount("Введите сумму:"));
            else if (num == 4) {
                interestChecker.shutdown();
                return;
            } else System.out.println("Unknown command");
        }
    }

    private static Long initAccountNumber(String message) {
        System.out.println(message);
        Long accountNumber = scanner.nextLong();
        scanner.nextLine();
        return accountNumber;
    }

    private static String initBankName(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    private static BigDecimal initAmount(String message) {
        System.out.println(message);
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        return amount;
    }
}
