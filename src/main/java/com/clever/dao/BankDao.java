package com.clever.dao;

import com.clever.entity.Bank;

import java.util.Optional;

public interface BankDao {
    Optional<Bank> findByName(String name);

    Optional<Bank> findById(Long id);
}
