package com.clever.dao;

import com.clever.entity.Account;

import java.sql.Connection;

public interface AccountDao {

    Account saveOrUpdate(Account account);

    Account saveOrUpdate(Account account, Connection connection);
}
