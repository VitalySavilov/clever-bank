package com.clever.dao;

import com.clever.entity.Account;
import com.clever.entity.AppUser;
import com.clever.entity.Bank;
import com.clever.exception.AccountException;
import com.clever.exception.AppUserException;
import com.clever.util.ConnectionManager;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AppUserDaoImpl implements AppUserDao{
    private static final AppUserDaoImpl INSTANCE = new AppUserDaoImpl();
    private static final String FIND_BY_ID_SQL = """
            SELECT id,
                firstname,
                lastname,
                patronymic
            FROM app_user
            WHERE id = ?;
            """;

    private AppUserDaoImpl() {
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        AppUser appUser = null;
        try {
            @Cleanup Connection connection = ConnectionManager.getConnection();
            @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
            preparedStatement.setLong(1, id);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                appUser = AppUser.builder()
                        .id(resultSet.getLong("id"))
                        .firstName(resultSet.getString("firstname"))
                        .lastName(resultSet.getString("lastname"))
                        .patronymic(resultSet.getString("patronymic"))
                        .build();
            }
        } catch (SQLException e) {
            throw new AppUserException(
                    String.format("Cannot find user with id = %s", id), e);
        }
        return Optional.ofNullable(appUser);
    }

    public static AppUserDaoImpl getInstance(){
        return INSTANCE;
    }
}
