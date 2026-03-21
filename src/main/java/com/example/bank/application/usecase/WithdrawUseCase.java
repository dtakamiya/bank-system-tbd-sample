package com.example.bank.application.usecase;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.AccountRepository;
import com.example.bank.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WithdrawUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public WithdrawUseCase(AccountRepository accountRepository,
                           TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Account execute(AccountNumber accountNumber, Money amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        Account withdrawn = account.withdraw(amount);
        accountRepository.save(withdrawn);

        Transaction transaction = Transaction.withdrawal(
                accountNumber, amount, withdrawn.getBalance());
        transactionRepository.save(transaction);

        return withdrawn;
    }
}
