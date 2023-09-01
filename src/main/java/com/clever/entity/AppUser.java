package com.clever.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class AppUser {
    private Long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private List<Account> accounts;
}
