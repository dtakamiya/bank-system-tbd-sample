package com.example.bank.application.usecase;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetTransactionHistoryUseCase {

    private final TransactionRepository transactionRepository;

    public GetTransactionHistoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> execute(AccountNumber accountNumber, int page, int size) {
        return transactionRepository.findByAccountNumber(accountNumber, page, size);
    }
}
