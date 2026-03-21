package com.example.bank.application.usecase;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class GetAccountUseCase {

    private final AccountRepository accountRepository;

    public GetAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account execute(AccountNumber accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
