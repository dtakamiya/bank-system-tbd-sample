package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 取引履歴の取得を行うユースケース。
 *
 * <p>フィーチャーフラグによる制御のもと、指定された口座の取引履歴をページネーション付きで取得する。</p>
 */
@Service
public class GetTransactionHistoryUseCase {

    private final TransactionRepository transactionRepository;
    private final FeatureFlagService featureFlagService;

    public GetTransactionHistoryUseCase(TransactionRepository transactionRepository,
                                        FeatureFlagService featureFlagService) {
        this.transactionRepository = transactionRepository;
        this.featureFlagService = featureFlagService;
    }

    /**
     * 指定された口座の取引履歴を取得する。
     *
     * @param accountNumber 取得対象の口座番号
     * @param page ページ番号
     * @param size 1ページあたりの件数
     * @return 取引履歴のリスト
     * @throws FeatureDisabledException 取引履歴機能が無効化されている場合
     */
    public List<Transaction> execute(AccountNumber accountNumber, int page, int size) {
        if (!featureFlagService.isEnabled("transaction-history")) {
            throw new FeatureDisabledException("transaction-history");
        }
        return transactionRepository.findByAccountNumber(accountNumber, page, size);
    }
}
