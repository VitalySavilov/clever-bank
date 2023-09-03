package com.clever.dao;

import com.clever.entity.ShedLock;
import com.clever.util.ConnectionManager;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class ShedLockDaoImpl implements ShedLockDao {
    private static final ShedLockDaoImpl INSTANCE = new ShedLockDaoImpl();
    private static final String SAVE_SQL = """
            INSERT INTO shedlock (lock_until, locked_at, id)
            VALUES (?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE shedlock
            SET lock_until = ?,
                locked_at = ?
            WHERE id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id,
                locked_at,
                lock_until
            FROM shedlock
            WHERE id = ?;
            """;

    private ShedLockDaoImpl() {
    }

    @SneakyThrows
    public void tryToLock(ShedLock lock, Connection connection) {
        @Cleanup PreparedStatement preparedStatement = findById(lock.getId()).isPresent() ?
                connection.prepareStatement(UPDATE_SQL) :
                connection.prepareStatement(SAVE_SQL);
        preparedStatement.setTimestamp(1, lock.getLockUntil());
        preparedStatement.setTimestamp(2, lock.getLockAt());
        preparedStatement.setLong(3, lock.getId());
        preparedStatement.executeUpdate();
    }

    @SneakyThrows
    public Optional<ShedLock> findById(Long id) {
        @Cleanup Connection connection = ConnectionManager.getConnection();
        @Cleanup PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL);
        preparedStatement.setLong(1, id);
        @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
        ShedLock shedLock = null;
        if (resultSet.next()) {
            shedLock = ShedLock.builder()
                    .id(resultSet.getLong("id"))
                    .lockUntil(resultSet.getTimestamp("lock_until"))
                    .lockAt(resultSet.getTimestamp("locked_at"))
                    .build();
        }
        return Optional.ofNullable(shedLock);
    }

    public static ShedLockDaoImpl getInstance() {
        return INSTANCE;
    }
}
