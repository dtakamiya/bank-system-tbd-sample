package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.application.port.TransferFeePolicy;
import com.example.bank.domain.model.AccountNotFoundException;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.model.TransferResult;
import com.example.bank.domain.repository.AccountRepository;
import com.example.bank.domain.repository.TransactionRepository;
import com.example.bank.domain.service.TransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 口座間送金を行うユースケース。
 *
 * <p>フィーチャーフラグによる制御のもと、手数料計算とドメインサービスによる
 * 送金処理を実行し、取引履歴を記録する。</p>
 */
@Service
public class TransferUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final FeatureFlagService featureFlagService;
    private final TransferFeePolicy transferFeePolicy;
    private final TransferService transferService;

    public TransferUseCase(AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           FeatureFlagService featureFlagService,
                           TransferFeePolicy transferFeePolicy,
                           TransferService transferService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.featureFlagService = featureFlagService;
        this.transferFeePolicy = transferFeePolicy;
        this.transferService = transferService;
    }

    /**
     * 口座間送金を実行する。
     *
     * @param sourceAccountNumber 送金元口座番号
     * @param targetAccountNumber 送金先口座番号
     * @param amount              送金額
     * @return 送金結果
     * @throws FeatureDisabledException 送金機能が無効化されている場合
     * @throws AccountNotFoundException 口座が存在しない場合
     */
    @Transactional
    public TransferResult execute(AccountNumber sourceAccountNumber,
                                  AccountNumber targetAccountNumber,
                                  Money amount) {
        if (!featureFlagService.isEnabled("account-transfer")) {
            throw new FeatureDisabledException("account-transfer");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(sourceAccountNumber));

        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountNumber));

        Money fee = transferFeePolicy.calculateFee(amount);

        TransferResult result = transferService.transfer(sourceAccount, targetAccount, amount, fee);

        accountRepository.save(result.sourceAccount());
        accountRepository.save(result.targetAccount());

        Transaction transferOut = Transaction.transferOut(
                sourceAccountNumber, amount, fee,
                result.sourceAccount().getBalance(), targetAccountNumber);
        transactionRepository.save(transferOut);

        Transaction transferIn = Transaction.transferIn(
                targetAccountNumber, amount,
                result.targetAccount().getBalance(), sourceAccountNumber);
        transactionRepository.save(transferIn);

        return result;
    }
}
