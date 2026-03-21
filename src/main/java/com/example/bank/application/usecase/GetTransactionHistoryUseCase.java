package com.example.bank.application.usecase;

import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.FeatureDisabledException;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTransactionHistoryUseCase {

    private final TransactionRepository transactionRepository;
    private final FeatureFlagService featureFlagService;

    public GetTransactionHistoryUseCase(TransactionRepository transactionRepository,
                                        FeatureFlagService featureFlagService) {
        this.transactionRepository = transactionRepository;
        this.featureFlagService = featureFlagService;
    }

    public List<Transaction> execute(AccountNumber accountNumber, int page, int size) {
        if (!featureFlagService.isEnabled("transaction-history")) {
            throw new FeatureDisabledException("transaction-history");
        }
        return transactionRepository.findByAccountNumber(accountNumber, page, size);
    }
}
