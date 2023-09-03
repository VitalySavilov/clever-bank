package com.clever.dao;

import com.clever.entity.Bank;
import com.clever.exception.BankNotFoundException;
import com.clever.util.ConnectionManager;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class BankDaoImpl implements BankDao {
    private static final BankDaoImpl INSTANCE = new BankDaoImpl();
    private static final String FIND_BY_NAME_SQL = """
            SELECT id,
                bank_name
            FROM bank
            WHERE bank_name = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id,
                bank_name
            FROM bank
            WHERE id = ?;
            """;

    private BankDaoImpl() {
    }

    @Override
    public Optional<Bank> findByName(String name) {
        Optional<Bank> result;
        try {
            @Cleanup Connection connection = ConnectionManager.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NAME_SQL);
            preparedStatement.setString(1, name);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
            result = buildBank(resultSet);
        } catch (SQLException e) {
            throw new BankNotFoundException(String.format("Cannot found bank %s", name), e);
        }
        return result;
    }

    @Override
    public Optional<Bank> findById(Long id) {
        Optional<Bank> result;
        try {
            @Cleanup Connection connection = ConnectionManager.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setLong(1, id);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
            result = buildBank(resultSet);
        } catch (SQLException e) {
            throw new BankNotFoundException("Cannot found bank", e);
        }
        return result;
    }

    private Optional<Bank> buildBank(ResultSet resultSet) throws SQLException {
        Bank bank = null;
        if (resultSet.next()) {
            bank = Bank.builder()
                    .id(resultSet.getLong("id"))
                    .name(resultSet.getString("bank_name"))
                    .build();
        }
        return Optional.ofNullable(bank);
    }

    public static BankDaoImpl getInstance() {
        return INSTANCE;
    }
}
