package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.application.port.WithdrawalPolicy;
import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.AccountRepository;
import com.example.bank.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 口座からの出金を行うユースケース。
 *
 * <p>フィーチャーフラグによる制御のもと、出金ポリシーに基づいて出金を実行し、
 * 取引履歴を記録する。</p>
 */
@Service
public class WithdrawUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FeatureFlagService featureFlagService;
    private final WithdrawalPolicy withdrawalPolicy;

    public WithdrawUseCase(AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           FeatureFlagService featureFlagService,
                           WithdrawalPolicy withdrawalPolicy) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.featureFlagService = featureFlagService;
        this.withdrawalPolicy = withdrawalPolicy;
    }

    /**
     * 指定された口座から出金を実行する。
     *
     * @param accountNumber 出金元の口座番号
     * @param amount 出金希望額
     * @return 出金後の口座
     * @throws FeatureDisabledException 出金機能が無効化されている場合
     * @throws AccountNotFoundException 指定された口座番号の口座が存在しない場合
     */
    @Transactional
    public Account execute(AccountNumber accountNumber, Money amount) {
        if (!featureFlagService.isEnabled("withdrawal")) {
            throw new FeatureDisabledException("withdrawal");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        WithdrawalResult result = withdrawalPolicy.withdraw(account, amount);
        accountRepository.save(result.updatedAccount());

        Transaction transaction = Transaction.withdrawal(
                accountNumber, result.withdrawnAmount(), result.updatedAccount().getBalance());
        transactionRepository.save(transaction);

        return result.updatedAccount();
    }
}
