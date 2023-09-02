package com.clever.dao;

import com.clever.entity.BankTransaction;
import com.clever.exception.BankTransactionException;
import com.clever.util.ConnectionManager;
import lombok.Cleanup;

import java.sql.*;

public class BankTransactionDaoImpl implements BankTransactionDao {
    private static final BankTransactionDaoImpl INSTANCE = new BankTransactionDaoImpl();
    private static final String SAVE_SQL = """
            INSERT INTO bank_transaction (amount, transaction_timestamp, transaction_type, account_id) 
            VALUES (?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE bank_transaction
            SET amount = ?,
                transaction_timestamp = ?,
                transaction_type = ?,
                account_id = ?
            WHERE id = ?;
            """;

    private BankTransactionDaoImpl() {
    }

    public BankTransaction saveOrUpdate(BankTransaction transaction) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        return saveOrUpdate(transaction, connection);
    }

    public BankTransaction saveOrUpdate(BankTransaction transaction, Connection connection) {
        boolean isIdPresent = transaction.getId() != null;
        try {
            @Cleanup PreparedStatement preparedStatement = isIdPresent ? connection.prepareStatement(UPDATE_SQL) :
                    connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setBigDecimal(1, transaction.getAmount());
            preparedStatement.setTimestamp(2, transaction.getTransactionTimestamp());
            preparedStatement.setString(3, transaction.getType());
            preparedStatement.setLong(4, transaction.getAccount().getId());
            if (isIdPresent) preparedStatement.setLong(5, transaction.getId());
            preparedStatement.executeUpdate();
            @Cleanup ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) transaction.setId(generatedKeys.getLong("id"));
        } catch (SQLException e) {
            throw new BankTransactionException("Cannot save or update bank transaction", e);
        }
        return transaction;
    }

    public static BankTransactionDaoImpl getInstance() {
        return INSTANCE;
    }
}
