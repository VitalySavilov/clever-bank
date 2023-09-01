package com.clever.dao;

import com.clever.entity.Account;
import com.clever.exception.AccountException;
import com.clever.util.ConnectionManager;
import lombok.Cleanup;

import java.sql.*;

public class AccountDaoImpl implements AccountDao {
    private static final AccountDaoImpl INSTANCE = new AccountDaoImpl();
    private static final String SAVE_SQL = """
            INSERT INTO account (account_number, card_number, open_date, balance, currency, bank_id, app_user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE account
            SET account_number = ?,
                card_number = ?,
                open_date = ?,
                balance = ?,
                currency = ?,
                bank_id = ?,
                app_user_id = ?
            WHERE id = ?;
            """;

    private AccountDaoImpl() {
    }

    @Override
    public Account saveOrUpdate(Account account) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        return saveOrUpdate(account, connection);
    }

    @Override
    public Account saveOrUpdate(Account account, Connection connection) {
        boolean isIdPresent = account.getId() != null;
        try {
            @Cleanup PreparedStatement preparedStatement = isIdPresent ? connection.prepareStatement(UPDATE_SQL) :
                    connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, account.getAccountNumber());
            preparedStatement.setString(2, account.getCardNumber());
            preparedStatement.setDate(3, account.getOpenDate());
            preparedStatement.setBigDecimal(4, account.getBalance());
            preparedStatement.setString(5, account.getCurrency());
            preparedStatement.setLong(6, account.getBank().getId());
            preparedStatement.setLong(7, account.getAppUser().getId());
            if (isIdPresent) preparedStatement.setLong(8, account.getId());
            preparedStatement.executeUpdate();
            @Cleanup ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) account.setId(generatedKeys.getLong("id"));
        } catch (SQLException e) {
            throw new AccountException("Cannot save or update account", e);
        }
        return account;
    }

    public static AccountDaoImpl getInstance() {
        return INSTANCE;
    }

}
