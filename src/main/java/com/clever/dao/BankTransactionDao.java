package com.clever.dao;

import com.clever.entity.BankTransaction;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface BankTransactionDao {

    BankTransaction saveOrUpdate(BankTransaction transaction);

    BankTransaction saveOrUpdate(BankTransaction transaction, Connection connection);

    void saveTransfer(Long transactionId, Long enemyAccountId, Connection connection);

    List<BankTransaction> getList(Long accountId);

    Optional<Long> findTransferEnemyId(Long transactionId);
}
