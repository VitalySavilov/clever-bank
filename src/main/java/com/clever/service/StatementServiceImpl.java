package com.clever.service;

import com.clever.dao.*;
import com.clever.entity.*;
import com.clever.exception.AccountException;
import com.clever.exception.AppUserException;
import com.clever.exception.BankNotFoundException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class StatementServiceImpl {
    private static final StatementServiceImpl INSTANCE = new StatementServiceImpl();
    private final AccountDao accountDao = AccountDaoImpl.getInstance();
    private final BankDao bankDao = BankDaoImpl.getInstance();
    private final AppUserDao appUserDao = AppUserDaoImpl.getInstance();
    private final BankTransactionDao bankTransactionDao = BankTransactionDaoImpl.getInstance();

    private StatementServiceImpl() {
    }

    public void printStatement(Long accountNumber, String bankName, LocalDate dateFrom) {
        Document document = new Document();
        File file = new File("statement");
        if (!file.exists()) file.mkdir();
        Bank bank = bankDao.findByName(bankName).orElseThrow(() ->
                new BankNotFoundException(String.format("Cannot find bank %s", bankName)));
        Account account = accountDao.findByAccountNumber(accountNumber, bank.getId()).orElseThrow(() ->
                new AccountException(String.format("Cannot find account with number %s", accountNumber)));
        AppUser appUser = appUserDao.findById(account.getAppUser().getId()).orElseThrow(() ->
                new AppUserException("Cannot find user"));
        List<BankTransaction> transactionList = bankTransactionDao.getList(account.getId());
        file = new File(String.format("statement/%s.pdf", account.getAccountNumber()));
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            StringBuilder stringBuilder = new StringBuilder()
                    .append(String.format("%20s%n", "Выписка"))
                    .append(String.format("%20s%n", bank.getName()))
                    .append(String.format("%-20s | %-40s%n", "Клиент",
                            String.format("%s %s %s", appUser.getLastName(), appUser.getFirstName(),
                                    appUser.getPatronymic())))
                    .append(String.format("%-20s | %-40s%n", "Счет", account.getCardNumber()))
                    .append(String.format("%-20s | %-40s%n", "Валюта", account.getCurrency()))
                    .append(String.format("%-20s | %-40s%n", "Дата открытия", account.getOpenDate()))
                    .append(String.format("%-20s | %-40s%n", "Период",
                            String.format("%1$td.%1$tm.%1$tY - %2$td.%2$tm.%2$tY", dateFrom, LocalDate.now())))
                    .append(String.format("%-20s | %-40s%n", "Дата и время формирования",
                            String.format("%1$td.%1$tm.%1$tY, %1$tH.%1$tM", LocalDateTime.now())))
                    .append(String.format("%-20s | %-40s%n", "Остаток",
                            String.format("%.2f %s", account.getBalance(), account.getCurrency())))
                    .append(String.format("   %-5s      |%-20s     | %30s %n", "Дата", "Примечание", "Сумма"))
                    .append("------------------------------------------------------------");
            for (BankTransaction transaction : transactionList) {
                if (transaction.getType().equals(BankTransactionType.TRANSFER.getValue())) {
                    Optional<Long> enemyAccountId = bankTransactionDao.findTransferEnemyId(transaction.getId());
                    AppUser enemyAppUser = null;
                    if (enemyAccountId.isPresent()) {
                        Account enemyAccount = accountDao.findById(enemyAccountId.get()).orElseThrow(() ->
                                new AccountException("Cannot find account"));
                        enemyAppUser = appUserDao.findById(enemyAccount.getAppUser().getId()).orElseThrow(() ->
                                new AppUserException("Cannot find user"));
                    }
                    if(enemyAppUser != null) {
                        stringBuilder.append(String.format("%s | %s | %s",
                                String.format("%1$td.%1$tm.%1$tY", transaction.getTransactionTimestamp()),
                                String.format("%s от %sа", transaction.getType(), enemyAppUser.getLastName()),
                                String.format("%s %s", transaction.getAmount(), account.getCurrency())));
                    } else {
                        stringBuilder.append(String.format("%s | %s | %s",
                                String.format("%1$td.%1$tm.%1$tY", transaction.getTransactionTimestamp()),
                                String.format("%s", transaction.getType()),
                                String.format("%s %s", transaction.getAmount(), account.getCurrency())));
                    }
                }
            }
            document.add(new Paragraph(stringBuilder.toString()));
            document.close();
            writer.close();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getInstance().printStatement(1235648975L, "Clever-Bank", LocalDate.now());
    }


    public static StatementServiceImpl getInstance() {
        return INSTANCE;
    }
}
