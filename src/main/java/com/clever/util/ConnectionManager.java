package com.clever.util;

import com.clever.exception.DataBaseConnectionException;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class ConnectionManager {
    private static final Properties PROPERTIES = PropertiesUtil.getProperties();
    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        initDataSource();
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DataBaseConnectionException(e);
        }
    }

    private static void initDataSource() {
        dataSource.setUsername(PROPERTIES.getProperty("db.username"));
        dataSource.setPassword(PROPERTIES.getProperty("db.password"));
        dataSource.setUrl(PROPERTIES.getProperty("db.url"));
    }
}
