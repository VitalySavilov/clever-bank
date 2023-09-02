package com.clever.dao;

import com.clever.entity.BankTransaction;

import java.sql.Connection;

public interface BankTransactionDao {

    BankTransaction saveOrUpdate(BankTransaction transaction);

    BankTransaction saveOrUpdate(BankTransaction transaction, Connection connection);

    void saveTransfer(Long transactionId, Long enemyAccountId, Connection connection);
}
