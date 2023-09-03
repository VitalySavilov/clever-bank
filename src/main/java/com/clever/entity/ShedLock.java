package com.clever.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
public class ShedLock {
    private Long id;
    private Timestamp lockUntil;
    private Timestamp lockAt;
}
