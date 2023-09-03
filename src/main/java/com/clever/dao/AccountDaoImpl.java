package com.clever.dao;

import com.clever.entity.Account;
import com.clever.entity.AppUser;
import com.clever.entity.Bank;
import com.clever.exception.AccountException;
import com.clever.util.ConnectionManager;
import lombok.Cleanup;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private static final String FIND_BY_ID_SQL = """
            SELECT id,
                account_number,
                card_number,
                open_date,
                balance,
                currency,
                bank_id,
                app_user_id
            FROM account
            WHERE id = ?;
            """;
    private static final String FIND_BY_ACCOUNT_NUMBER_SQL = """
            SELECT id,
                account_number,
                card_number,
                open_date,
                balance,
                currency,
                bank_id,
                app_user_id
            FROM account
            WHERE account_number = ? AND bank_id = ?;
            """;
    private static final String FIND_ID_LIST_SQL = """
            SELECT id
            FROM account;     
            """;

    private AccountDaoImpl() {
    }

    @Override
    public Account saveOrUpdate(Account account) {
        try {
            @Cleanup Connection connection = ConnectionManager.getConnection();
            return saveOrUpdate(account, connection);
        } catch (Exception e) {
            throw new AccountException("Cannot save or update account", e);
        }
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

    @Override
    public Optional<Account> findById(Long id) {
        Optional<Account> result;
        try {
            @Cleanup Connection connection = ConnectionManager.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setLong(1, id);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
            result = buildAccount(resultSet);
        } catch (SQLException e) {
            throw new AccountException(
                    String.format("Cannot find account with id = %s", id), e);
        }
        return result;
    }

    public Optional<Account> findByAccountNumber(Long accountNumber, Long bankId) {
        Optional<Account> result;
        try {
            @Cleanup Connection connection = ConnectionManager.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ACCOUNT_NUMBER_SQL);
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setLong(2, bankId);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
            result = buildAccount(resultSet);
        } catch (SQLException e) {
            throw new AccountException(
                    String.format("Cannot find account number = %s", accountNumber), e);
        }
        return result;
    }

    public List<Long> getIdList() {
        List<Long> result = new ArrayList<>();
        try {
            @Cleanup Connection connection = ConnectionManager.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(FIND_ID_LIST_SQL);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            throw new AccountException("Cannot get accounts id list", e);
        }

        return result;
    }

    private Optional<Account> buildAccount(ResultSet resultSet) throws SQLException {
        Account account = null;
        if (resultSet.next()) {
            account = Account.builder()
                    .id(resultSet.getLong("id"))
                    .accountNumber(resultSet.getLong("account_number"))
                    .cardNumber(resultSet.getString("card_number"))
                    .openDate(resultSet.getDate("open_date"))
                    .balance(resultSet.getBigDecimal("balance"))
                    .currency(resultSet.getString("currency"))
                    .bank(Bank.builder()
                            .id(resultSet.getLong("bank_id"))
                            .build())
                    .appUser(AppUser.builder()
                            .id(resultSet.getLong("app_user_id"))
                            .build())
                    .build();
        }
        return Optional.ofNullable(account);
    }

    public static AccountDaoImpl getInstance() {
        return INSTANCE;
    }

}
