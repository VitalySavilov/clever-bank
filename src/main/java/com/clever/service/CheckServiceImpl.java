package com.clever.service;

import com.clever.entity.BankTransaction;
import com.clever.exception.GenerateCheckException;

import java.io.*;

public class CheckServiceImpl implements CheckService {
    private static final CheckServiceImpl INSTANCE = new CheckServiceImpl();

    @Override
    public void printCheck(BankTransaction bankTransaction) {
        File file = new File("check");
        if (!file.exists()) file.mkdir();
        file = new File(String.format("check/%s", bankTransaction.getId()));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--------------------------------------------------\n")
                .append("|                 Банковский Чек                 |\n")
                .append(String.format("| %-20s %25d |%n", "Чек:", bankTransaction.getId()))
                .append(String.format("| %-20s %25s |%n",
                        String.format("%1$td-%1$tm-%1$tY", bankTransaction.getTransactionTimestamp()),
                        String.format("%1$TT", bankTransaction.getTransactionTimestamp())))
                .append(String.format("| %-20s %25s |%n", "Тип транзакции:", bankTransaction.getType()))
                .append(String.format("| %-20s %25s |%n",
                        "Банк:", bankTransaction.getAccount().getBank().getName()))
                .append(String.format("| %-20s %25d |%n",
                        "Номер счета:", bankTransaction.getAccount().getAccountNumber()))
                .append(String.format("| %-20s %21.2f %s |%n",
                        "Сумма:", bankTransaction.getAmount(), bankTransaction.getAccount().getCurrency()))
                .append("--------------------------------------------------\n");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true)))) {
            writer.write(stringBuilder.toString());
            System.out.println(stringBuilder);
        } catch (IOException e) {
            throw new GenerateCheckException("Failed to generate check", e);
        }
    }

    @Override
    public void printCheck(BankTransaction bankTransactionFrom, BankTransaction bankTransactionTo) {
        File file = new File("check");
        if (!file.exists()) file.mkdir();
        file = new File(String.format("check/%s", bankTransactionFrom.getId()));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--------------------------------------------------\n")
                .append("|                 Банковский Чек                 |\n")
                .append(String.format("| %-20s %25d |%n", "Чек:", bankTransactionFrom.getId()))
                .append(String.format("| %-20s %25s |%n",
                        String.format("%1$td-%1$tm-%1$tY", bankTransactionFrom.getTransactionTimestamp()),
                        String.format("%1$TT", bankTransactionFrom.getTransactionTimestamp())))
                .append(String.format("| %-20s %25s |%n", "Тип транзакции:", bankTransactionFrom.getType()))
                .append(String.format("| %-20s %25s |%n", "Банк отправителя:",
                        bankTransactionFrom.getAccount().getBank().getName()))
                .append(String.format("| %-20s %25s |%n", "Банк получателя:",
                        bankTransactionTo.getAccount().getBank().getName()))
                .append(String.format("| %-20s %25d |%n", "Счет отправителя:",
                        bankTransactionFrom.getAccount().getAccountNumber()))
                .append(String.format("| %-20s %25d |%n", "Счет получателя:",
                        bankTransactionTo.getAccount().getAccountNumber()))
                .append(String.format("| %-20s %21.2f %s |%n", "Сумма:",
                        bankTransactionTo.getAmount(), bankTransactionTo.getAccount().getCurrency()))
                .append("--------------------------------------------------\n");
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, true)))) {
            writer.write(stringBuilder.toString());
            System.out.println(stringBuilder);
        } catch (IOException e) {
            throw new GenerateCheckException("Failed to generate check", e);
        }
    }

    public static CheckServiceImpl getInstance() {
        return INSTANCE;
    }
}
