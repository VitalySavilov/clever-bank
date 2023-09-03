package com.clever.service;

import com.clever.entity.BankTransaction;

public interface CheckService {

    void printCheck(BankTransaction bankTransaction);

    void printCheck(BankTransaction bankTransactionFrom, BankTransaction bankTransactionTo);
}
