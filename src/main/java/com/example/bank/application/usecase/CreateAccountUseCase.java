package com.example.bank.application.usecase;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateAccountUseCase {

    private final AccountRepository accountRepository;

    public CreateAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account execute(String ownerName) {
        Account account = Account.create(ownerName);
        return accountRepository.save(account);
    }
}
