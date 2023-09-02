package com.clever.service;

import com.clever.dao.AccountDao;
import com.clever.dao.AccountDaoImpl;
import com.clever.dao.BankTransactionDao;
import com.clever.dao.BankTransactionDaoImpl;
import com.clever.entity.Account;
import com.clever.entity.BankTransaction;
import com.clever.entity.BankTransactionType;
import com.clever.exception.AccountException;
import com.clever.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AccountServiceImpl implements AccountService {
    private final AccountDao accountDao = AccountDaoImpl.getInstance();
    private final BankTransactionDao bankTransactionDao = BankTransactionDaoImpl.getInstance();
    private static final AccountServiceImpl INSTANCE = new AccountServiceImpl();

    private AccountServiceImpl() {
    }

    @Override
    public Account replenish(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().subtract(amount));
        try {
            updateInTransaction(account, amount, BankTransactionType.REPLENISH);
        } catch (SQLException e) {
            throw new AccountException("Cannot replenish account", e);
        }
        return account;
    }

    @Override
    public Account withdraw(Account account, BigDecimal amount) {
        try {
            updateInTransaction(account, amount, BankTransactionType.WITHDRAW);
        } catch (SQLException e) {
            throw new AccountException("Cannot withdraw from account", e);
        }
        return account;
    }

    private void updateInTransaction(Account account,
                                     BigDecimal amount,
                                     BankTransactionType type) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);
            accountDao.saveOrUpdate(account, connection);
            bankTransactionDao.saveOrUpdate(
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
    }

    private BankTransaction buildBankTransaction(BigDecimal amount, Account account, BankTransactionType type) {
        return BankTransaction.builder()
                .amount(amount)
                .transactionTimestamp(Timestamp.valueOf(LocalDateTime.now()))
                .account(account)
                .type(type.getValue())
                .build();
    }

    public static AccountServiceImpl getInstance() {
        return INSTANCE;
    }

}