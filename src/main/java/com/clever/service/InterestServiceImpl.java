package com.clever.service;

import com.clever.dao.*;
import com.clever.entity.Account;
import com.clever.entity.BankTransaction;
import com.clever.entity.BankTransactionType;
import com.clever.entity.ShedLock;
import com.clever.exception.ChargeInterestException;
import com.clever.util.ConnectionManager;
import com.clever.util.PropertiesUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

public class InterestServiceImpl implements InterestService {
    private final ShedLockDao shedLockDao = ShedLockDaoImpl.getInstance();
    private final AccountDao accountDao = AccountDaoImpl.getInstance();
    private final BankDao bankDao = BankDaoImpl.getInstance();
    private final CheckService checkService = CheckServiceImpl.getInstance();
    private final BankTransactionDao bankTransactionDao = BankTransactionDaoImpl.getInstance();
    private static final InterestServiceImpl INSTANCE = new InterestServiceImpl();

    private InterestServiceImpl() {
    }

    @Override
    public void chargeInterest() {
        if (isLastDayOfMonth(LocalDate.now().getDayOfMonth())) {
            List<Long> accountIdList = accountDao.getIdList();
            for (Long id : accountIdList) {
                Optional<ShedLock> mayBeShedlock = shedLockDao.findById(id);
                if (mayBeShedlock.isEmpty() || mayBeShedlock.stream()
                        .anyMatch(x -> isTimeForCharge(x.getLockUntil()))) {
                    Optional<Account> maybeAccount = accountDao.findById(id);
                    if (maybeAccount.isEmpty()) continue;
                    Account account = maybeAccount.get();
                    if (!isPositiveBalance(account.getBalance())) continue;
                    BigDecimal newBalance = mathNewBalance(account.getBalance());
                    BigDecimal amount = newBalance.subtract(account.getBalance());
                    bankDao.findById(account.getBank().getId()).ifPresent(account::setBank);
                    account.setBalance(newBalance);
                    ShedLock shedLock = buildShedLock(id);
                    try {
                        BankTransaction bankTransaction = chargeInTransaction(shedLock, account, amount);
                        checkService.printCheck(bankTransaction);
                    } catch (SQLException e) {
                        throw new ChargeInterestException(
                                String.format("Cannot charge interest to account %s", account.getAccountNumber()), e);
                    }
                }
            }
        }
    }

    private BankTransaction chargeInTransaction(ShedLock shedLock, Account account, BigDecimal amount) throws SQLException {
        Connection connection = null;
        BankTransaction bankTransaction;
        try {
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);
            shedLockDao.tryToLock(shedLock, connection);
            accountDao.saveOrUpdate(account, connection);
            bankTransaction = bankTransactionDao.saveOrUpdate(
                    buildBankTransaction(amount, account),
                    connection);
            connection.commit();
            connection.setAutoCommit(true);
            connection.close();
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return bankTransaction;
    }

    private ShedLock buildShedLock(Long id) {
        return ShedLock.builder()
                .id(id)
                .lockAt(Timestamp.valueOf(LocalDateTime.now()))
                .lockUntil(Timestamp.valueOf(LocalDateTime.of(LocalDate.now()
                                .plusMonths(1)
                                .with(TemporalAdjusters.lastDayOfMonth()),
                        LocalTime.MIDNIGHT)))
                .build();
    }

    private BankTransaction buildBankTransaction(BigDecimal amount, Account account) {
        return BankTransaction.builder()
                .amount(amount)
                .transactionTimestamp(Timestamp.valueOf(LocalDateTime.now()))
                .account(account)
                .type(BankTransactionType.INTEREST.getValue())
                .build();
    }

    private boolean isLastDayOfMonth(int dayNumber) {
        return dayNumber == LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
                .getDayOfMonth();
    }

    private boolean isTimeForCharge(Timestamp timestamp) {
        return Timestamp.valueOf(LocalDateTime.now()).compareTo(timestamp) > 0;
    }

    private BigDecimal mathNewBalance(BigDecimal balance) {
        return balance.add(balance.multiply(BigDecimal.valueOf(
                Double.parseDouble(PropertiesUtil.getProperties().getProperty("bank.interest")) / 100)));
    }

    private boolean isPositiveBalance(BigDecimal balance) {
        return balance.compareTo(BigDecimal.ZERO) > 0;
    }

    public static InterestServiceImpl getInstance() {
        return INSTANCE;
    }
}
