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

/**
 * 口座の解約を行うユースケース。
 *
 * <p>フィーチャーフラグによる制御のもと、口座を解約し、
 * 残高がある場合は払い戻しの取引履歴を記録する。</p>
 */
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

    /**
     * 指定された口座を解約する。
     *
     * <p>残高がある場合は払い戻し取引を記録したうえで口座を閉鎖する。</p>
     *
     * @param accountNumber 解約対象の口座番号
     * @return 解約後の口座
     * @throws FeatureDisabledException 口座解約機能が無効化されている場合
     * @throws AccountNotFoundException 指定された口座番号の口座が存在しない場合
     */
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
