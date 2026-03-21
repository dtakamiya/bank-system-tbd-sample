package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.application.port.WithdrawalPolicy;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.AccountRepository;
import com.example.bank.domain.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CloseAccountUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FeatureFlagService featureFlagService;
    // 払い戻しはAccount.close()が担う。手数料は適用しない。
    // 将来Policy経由に変更する際の準備として保持。
    private final WithdrawalPolicy standardWithdrawalPolicy;

    public CloseAccountUseCase(AccountRepository accountRepository,
                               TransactionRepository transactionRepository,
                               FeatureFlagService featureFlagService,
                               @Qualifier("standardWithdrawalPolicy") WithdrawalPolicy standardWithdrawalPolicy) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.featureFlagService = featureFlagService;
        this.standardWithdrawalPolicy = standardWithdrawalPolicy;
    }

    @Transactional
    public Account execute(AccountNumber accountNumber) {
        if (!featureFlagService.isEnabled("account-closure")) {
            throw new FeatureDisabledException("account-closure");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        Money balanceBefore = account.getBalance();
        Account closed = account.close();

        if (balanceBefore.isPositive()) {
            Transaction refund = Transaction.refund(
                    accountNumber, balanceBefore, closed.getBalance());
            transactionRepository.save(refund);
        }

        accountRepository.save(closed);

        return closed;
    }
}
