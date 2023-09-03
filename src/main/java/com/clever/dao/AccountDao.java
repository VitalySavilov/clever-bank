package com.clever.dao;

import com.clever.entity.Account;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface AccountDao {

    Account saveOrUpdate(Account account);

    Account saveOrUpdate(Account account, Connection connection);

    Optional<Account> findById(Long id);

    Optional<Account> findByAccountNumber(Long accountNumber, Long bankId);

    List<Long> getIdList();
}
