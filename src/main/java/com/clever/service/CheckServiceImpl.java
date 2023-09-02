package com.clever.service;

import com.clever.entity.BankTransaction;
import com.clever.exception.GenerateCheckException;

import java.io.*;

public class CheckServiceImpl implements CheckService {
    private static final CheckServiceImpl INSTANCE = new CheckServiceImpl();

    public void printCheck(BankTransaction bankTransaction) {
        File file  = new File("check");
        if (!file.exists()) file.mkdir();
        file = new File(String.format("check/%s", bankTransaction.getId()));
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true)))) {
            writer.write("--------------------------------------------------\n");
            writer.write("|                 Банковский Чек                 |\n");
            writer.write(String.format("| %-20s %25d |%n", "Чек:", bankTransaction.getId()));
            writer.write(String.format("| %-20s %25s |%n", String.format("%1$td-%1$tm-%1$tY", bankTransaction.getTransactionTimestamp()),
                    String.format("%1$TT", bankTransaction.getTransactionTimestamp())));
            writer.write(String.format("| %-20s %25s |%n", "Тип транзакции:", bankTransaction.getType()));
            writer.write(String.format("| %-20s %25d |%n", "Номер счета:", bankTransaction.getAccount().getAccountNumber()));
            writer.write(String.format("| %-20s %25.2f |%n", "Сумма:", bankTransaction.getAmount()));
            writer.write("--------------------------------------------------\n");

        } catch (IOException e) {
            throw new GenerateCheckException("Failed to generate check", e);
        }
    }


    public static CheckServiceImpl getInstance() {
        return INSTANCE;
    }
}
