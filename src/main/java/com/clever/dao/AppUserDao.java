package com.clever.dao;

import com.clever.entity.AppUser;

import java.util.Optional;

public interface AppUserDao {

    Optional<AppUser> findById (Long id);
}
