package com.example.bank.domain.repository;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Transaction;

import java.util.List;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findByAccountNumber(AccountNumber accountNumber, int page, int size);
}
