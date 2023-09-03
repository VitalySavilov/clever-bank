package com.clever.service;

import com.clever.dao.*;
import com.clever.entity.Account;
import com.clever.entity.Bank;
import com.clever.entity.BankTransaction;
import com.clever.entity.BankTransactionType;
import com.clever.exception.*;
import com.clever.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AccountServiceImpl implements AccountService {
    private final AccountDao accountDao = AccountDaoImpl.getInstance();
    private final BankTransactionDao bankTransactionDao = BankTransactionDaoImpl.getInstance();
    private final BankDao bankDaoImpl = BankDaoImpl.getInstance();
    private final CheckService checkService = CheckServiceImpl.getInstance();
    private static final AccountServiceImpl INSTANCE = new AccountServiceImpl();

    private AccountServiceImpl() {
    }

    @Override
    public Account replenish(Long fromAccountNumber, String bankName, BigDecimal amount) {
        Bank bank = findBank(bankName);
        Account account = findAccount(fromAccountNumber, bank.getId());
        account.setBank(bank);
        account.setBalance(account.getBalance().add(amount));
        try {
            BankTransaction bankTransaction = updateInTransaction(
                    account,
                    amount,
                    BankTransactionType.REPLENISH);
            checkService.printCheck(bankTransaction);
        } catch (SQLException e) {
            throw new AccountException("Cannot replenish account", e);
        }
        return account;
    }

    @Override
    public Account withdraw(Long fromAccountNumber, String bankName, BigDecimal amount) {
        Bank bank = findBank(bankName);
        Account account = findAccount(fromAccountNumber, bank.getId());
        checkBalance(account.getBalance(), amount);
        account.setBank(bank);
        try {
            account.setBalance(account.getBalance().subtract(amount));
            BankTransaction bankTransaction = updateInTransaction(
                    account,
                    amount.negate(),
                    BankTransactionType.WITHDRAW);
            checkService.printCheck(bankTransaction);
        } catch (SQLException e) {
            throw new AccountException("Cannot withdraw from account", e);
        }
        return account;
    }

    @Override
    public void transferMoney(Long fromAccountNumber, Long toAccountNumber,
                              String fromBankName, String toBankName, BigDecimal amount) {
        Bank fromBank = findBank(fromBankName);
        Bank toBank = findBank(toBankName);
        Account fromAccount = findAccount(fromAccountNumber, fromBank.getId());
        Account toAccount = findAccount(toAccountNumber, toBank.getId());
        checkCurrencyMatch(fromAccount.getCurrency(), toAccount.getCurrency());
        checkBalance(fromAccount.getBalance(), amount);
        fromAccount.setBank(fromBank);
        toAccount.setBank(toBank);
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        try {
            transferInTransaction(fromAccount, toAccount, amount);
        } catch (SQLException e) {
            throw new BankTransactionException(
                    String.format("Cannot transfer money from account %s to account %s",
                            fromAccountNumber, toAccountNumber), e);
        }
    }

    private Bank findBank(String bankName) {
        return bankDaoImpl.findByName(bankName)
                .orElseThrow(() -> new BankNotFoundException(
                        String.format("Cannot found bank %s", bankName)));
    }

    private Account findAccount(Long accountNumber, Long bankId) {
        return accountDao.findByAccountNumber(accountNumber, bankId)
                .orElseThrow(() -> new AccountException(
                        String.format("Cannot found account number %s", accountNumber)));
    }

    private void checkCurrencyMatch(String fromAccountCurrency, String toAccountCurrency) {
        if (!fromAccountCurrency.equals(toAccountCurrency)) {
            throw new CurrencyNotMatchException(String.format("Sender account currency %s " +
                            "does not match to recipient account currency %s",
                    fromAccountCurrency, toAccountCurrency));
        }
    }

    private BankTransaction updateInTransaction(Account account,
                                                BigDecimal amount,
                                                BankTransactionType type) throws SQLException {
        Connection connection = null;
        BankTransaction bankTransaction = null;
        try {
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);
            accountDao.saveOrUpdate(account, connection);
            bankTransaction = bankTransactionDao.saveOrUpdate(
                    buildBankTransaction(amount, account, type),
                    connection);
            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            if (connection != null) {
                connection.close();
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return bankTransaction;
    }

    private void transferInTransaction(Account fromAccount, Account toAccount, BigDecimal amount) throws SQLException {
        Connection connection = null;
        BankTransaction bankTransactionFrom;
        BankTransaction bankTransactionTo;
        try {
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);
            accountDao.saveOrUpdate(fromAccount, connection);
            bankTransactionFrom = bankTransactionDao.saveOrUpdate(
                    buildBankTransaction(amount.negate(), fromAccount, BankTransactionType.TRANSFER),
                    connection);
            accountDao.saveOrUpdate(toAccount, connection);
            bankTransactionTo = bankTransactionDao.saveOrUpdate(
                    buildBankTransaction(amount, toAccount, BankTransactionType.TRANSFER),
                    connection);
            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        checkService.printCheck(bankTransactionFrom, bankTransactionTo);
    }

    private BankTransaction buildBankTransaction(BigDecimal amount, Account account, BankTransactionType type) {
        return BankTransaction.builder()
                .amount(amount)
                .transactionTimestamp(Timestamp.valueOf(LocalDateTime.now()))
                .account(account)
                .type(type.getValue())
                .build();
    }

    private void checkBalance(BigDecimal fromAccountBalance, BigDecimal amount) {
        if (fromAccountBalance.compareTo(amount) < 0) {
            throw new NotSufficientFundsException("Insufficient funds");
        }
    }

    public static AccountServiceImpl getInstance() {
        return INSTANCE;
    }

}
