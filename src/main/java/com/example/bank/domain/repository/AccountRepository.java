package com.example.bank.domain.repository;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;

import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findByAccountNumber(AccountNumber accountNumber);
}
