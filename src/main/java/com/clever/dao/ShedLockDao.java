package com.clever.dao;

import com.clever.entity.ShedLock;

import java.sql.Connection;
import java.util.Optional;

public interface ShedLockDao {
    void tryToLock(ShedLock lock, Connection connection);
    Optional<ShedLock> findById(Long id);

}
